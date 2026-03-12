<?php
header("Content-Type: application/json");
ini_set('display_errors', 1);
error_reporting(E_ALL);

$db_host = "localhost";
$db_user = "root";
$db_pass = "";
$db_name = "viva";

$response = ['success' => false, 'message' => ''];

try {
    $con = new mysqli($db_host, $db_user, $db_pass, $db_name);
    
    if ($con->connect_error) {
        throw new Exception("Database connection failed: " . $con->connect_error);
    }

    $input = json_decode(file_get_contents('php://input'), true);
    
    if (json_last_error() !== JSON_ERROR_NONE) {
        throw new Exception("Invalid JSON input");
    }

    if (!isset($input['action']) || !isset($input['user_id'])) {
        throw new Exception("Missing required fields: action and user_id");
    }

    $action = $input['action'];
    $user_id = (int)$input['user_id'];
    $current_date = date('Y-m-d');

    $stmt = $con->prepare("SELECT ID FROM user_account WHERE ID = ?");
    $stmt->bind_param("i", $user_id);
    $stmt->execute();
    
    if ($stmt->get_result()->num_rows === 0) {
        throw new Exception("User not found");
    }

    switch ($action) {
        case 'save_result':
            if (!isset($input['completed_tasks']) || !isset($input['total_tasks'])) {
                throw new Exception("Missing required fields for saving results");
            }

            $completed_tasks = (int)$input['completed_tasks'];
            $total_tasks = (int)$input['total_tasks'];
            $default_completed = min($completed_tasks, 7);
            $custom_completed = max(0, $completed_tasks - 7);
            $custom_total = max(0, $total_tasks - 7);
            $percentage = $total_tasks > 0 ? ($completed_tasks * 100 / $total_tasks) : 0;

            $stmt = $con->prepare("
                INSERT INTO activity_results 
                (user_id, result_date, completed_tasks, total_tasks, percentage, default_completed, custom_completed, custom_total)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                ON DUPLICATE KEY UPDATE
                completed_tasks = VALUES(completed_tasks),
                total_tasks = VALUES(total_tasks),
                percentage = VALUES(percentage),
                default_completed = VALUES(default_completed),
                custom_completed = VALUES(custom_completed),
                custom_total = VALUES(custom_total)
            ");
            
            $stmt->bind_param(
                "isiiiiii", 
                $user_id, 
                $current_date,
                $completed_tasks,
                $total_tasks,
                $percentage,
                $default_completed,
                $custom_completed,
                $custom_total
            );
            
            if (!$stmt->execute()) {
                throw new Exception("Failed to save results: " . $stmt->error);
            }

            // Mark activities as completed for this user
            $update = $con->prepare("
                UPDATE activities 
                SET completed = 1, 
                    completed_date = ?
                WHERE user_id = ? 
                AND completed = 0
                AND activity_date = ?
            ");
            $update->bind_param("sis", $current_date, $user_id, $current_date);
            $update->execute();

            $response = [
                'success' => true,
                'message' => 'Results saved successfully',
                'result_id' => $stmt->insert_id
            ];
            break;

        case 'get_result':
            // Get today's results
            $stmt = $con->prepare("
                SELECT * FROM activity_results 
                WHERE user_id = ? AND result_date = ?
            ");
            $stmt->bind_param("is", $user_id, $current_date);
            $stmt->execute();
            $result = $stmt->get_result()->fetch_assoc();

            if (!$result) {
                // Check if activities were completed today
                $check = $con->prepare("
                    SELECT COUNT(*) as count FROM activities 
                    WHERE user_id = ? 
                    AND completed_date = ?
                ");
                $check->bind_param("is", $user_id, $current_date);
                $check->execute();
                $completed_count = $check->get_result()->fetch_assoc()['count'];

                if ($completed_count > 0) {
                    // Calculate results from activities
                    $calc = $con->prepare("
                        SELECT 
                            COUNT(*) as total_tasks,
                            SUM(completed) as completed_tasks
                        FROM activities
                        WHERE user_id = ?
                        AND activity_date = ?
                    ");
                    $calc->bind_param("is", $user_id, $current_date);
                    $calc->execute();
                    $calc_result = $calc->get_result()->fetch_assoc();

                    $response = [
                        'success' => true,
                        'result' => [
                            'completed_tasks' => (int)$calc_result['completed_tasks'],
                            'total_tasks' => (int)$calc_result['total_tasks'],
                            'default_completed' => min($calc_result['completed_tasks'], 7),
                            'custom_completed' => max(0, $calc_result['completed_tasks'] - 7),
                            'custom_total' => max(0, $calc_result['total_tasks'] - 7),
                            'percentage' => $calc_result['total_tasks'] > 0 ? 
                                ($calc_result['completed_tasks'] * 100 / $calc_result['total_tasks']) : 0
                        ]
                    ];
                } else {
                    $response = [
                        'success' => false,
                        'message' => 'No results found for today'
                    ];
                }
            } else {
                $response = [
                    'success' => true,
                    'result' => $result
                ];
            }
            break;

        default:
            throw new Exception("Invalid action specified");
    }

} catch (Exception $e) {
    $response = [
        'success' => false,
        'message' => $e->getMessage()
    ];
} finally {
    if (isset($con)) {
        $con->close();
    }
    echo json_encode($response);
}