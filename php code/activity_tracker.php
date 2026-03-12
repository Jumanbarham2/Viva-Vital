<?php
header('Content-Type: application/json');
ini_set('display_errors', 1);
error_reporting(E_ALL);

$db_host = "localhost";
$db_user = "root";
$db_pass = "";
$db_name = "viva";

$response = ['success' => false, 'error' => true, 'message' => 'Unknown error'];

try {
    $con = new mysqli($db_host, $db_user, $db_pass, $db_name);
    
    if ($con->connect_error) {
        throw new Exception("Database connection failed");
    }

    $input = json_decode(file_get_contents('php://input'), true);
    if (json_last_error() !== JSON_ERROR_NONE) {
        throw new Exception("Invalid JSON input");
    }

    // Validate required fields
    if (empty($input['user_id'])) {
        throw new Exception("User ID is required");
    }

    if (empty($input['action'])) {
        throw new Exception("Action is required");
    }

    // Verify user exists
    $stmt = $con->prepare("SELECT ID FROM user_account WHERE ID = ?");
    $stmt->bind_param("i", $input['user_id']);
    if (!$stmt->execute()) {
        throw new Exception("Error verifying user");
    }
    if ($stmt->get_result()->num_rows === 0) {
        throw new Exception("User not found");
    }

    // Handle different actions
    switch ($input['action']) {
        case 'add':
            if (empty($input['activity_text'])) {
                throw new Exception("Activity text is required");
            }

            $activity_text = trim($input['activity_text']);
            if (strlen($activity_text) > 255) {
                throw new Exception("Activity text too long");
            }

            $insert = $con->prepare("INSERT INTO activities (user_id, activity_text) VALUES (?, ?)");
            $insert->bind_param("is", $input['user_id'], $activity_text);
            
            if (!$insert->execute()) {
                throw new Exception("Failed to save activity: " . $con->error);
            }

            $response = [
                'success' => true,
                'error' => false,
                'activity_id' => $insert->insert_id,
                'activity_text' => $activity_text
            ];
            break;

        default:
            throw new Exception("Invalid action");
    }

} catch (Exception $e) {
    http_response_code(400);
    $response = [
        'success' => false,
        'error' => true,
        'message' => $e->getMessage()
    ];
} finally {
    if (isset($con)) $con->close();
    echo json_encode($response);
}