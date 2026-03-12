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
        throw new Exception("Database connection failed: " . $con->connect_error);
    }

    // Get JSON input
    $input = json_decode(file_get_contents('php://input'), true);
    
    if (json_last_error() !== JSON_ERROR_NONE) {
        throw new Exception("Invalid JSON input");
    }

    // Validate required fields
    $required = ['user_id', 'appointment_type', 'reminder_time'];
    foreach ($required as $field) {
        if (empty($input[$field])) {
            throw new Exception("Missing required field: $field");
        }
    }

    // Validate user exists
    $stmt = $con->prepare("SELECT ID FROM user_account WHERE ID = ?");
    $stmt->bind_param("i", $input['user_id']);
    $stmt->execute();
    $result = $stmt->get_result();

    if ($result->num_rows === 0) {
        throw new Exception("Invalid user ID");
    }

    // Insert appointment
    $insert = $con->prepare("INSERT INTO appointment_reminder 
        (user_id, appointment_type, reminder_time) 
        VALUES (?, ?, ?)");
    
    $insert->bind_param("iss", 
        $input['user_id'],
        $input['appointment_type'],
        $input['reminder_time']
    );

    if (!$insert->execute()) {
        throw new Exception("Failed to save appointment: " . $insert->error);
    }

    $response = [
        'success' => true,
        'message' => 'Appointment saved successfully',
        'appointment_id' => $insert->insert_id
    ];

} catch (Exception $e) {
    http_response_code(400);
    $response = [
        'success' => false,
        'message' => $e->getMessage()
    ];
} finally {
    if (isset($con)) $con->close();
    echo json_encode($response);
}
?>