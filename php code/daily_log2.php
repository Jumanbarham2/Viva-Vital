<?php
header('Content-Type: application/json');
ini_set('display_errors', 1);
error_reporting(E_ALL);

$db_host = "localhost";
$db_user = "root";
$db_pass = "";
$db_name = "viva";

try {
    $con = new mysqli($db_host, $db_user, $db_pass, $db_name);
    
    if ($con->connect_error) {
        throw new Exception("Connection failed: " . $con->connect_error);
    }

    // Get JSON input
    $input = json_decode(file_get_contents('php://input'), true);
    
    if (json_last_error() !== JSON_ERROR_NONE) {
        throw new Exception("Invalid JSON input");
    }

    // Validate required fields
    $required = ['user_id', 'feeling', 'symptoms', 'physical', 'stress'];
    foreach ($required as $field) {
        if (!isset($input[$field])) {
            throw new Exception("Missing required field: $field");
        }
    }

    // Authenticate user using ID
    $stmt = $con->prepare("SELECT ID FROM user_account WHERE ID = ?");
    $stmt->bind_param("i", $input['user_id']);
    $stmt->execute();
    $result = $stmt->get_result();

    if ($result->num_rows === 0) {
        throw new Exception("Invalid user ID");
    }

    // Insert daily log
    $insert = $con->prepare("INSERT INTO daily_status 
        (user_id, feeling, symptoms, physical, stress, comment) 
        VALUES (?, ?, ?, ?, ?, ?)");
    
    $comment = $input['comment'] ?? null;

    $insert->bind_param("issiis", 
        $input['user_id'],
        $input['feeling'],
        $input['symptoms'],
        $input['physical'],
        $input['stress'],
        $comment
    );

    if (!$insert->execute()) {
        throw new Exception("Failed to save log: " . $insert->error);
    }

    $response = [
        'success' => true,
        'error' => false,
        'log_id' => $insert->insert_id,
        'timestamp' => date('Y-m-d H:i:s')
    ];

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
?>