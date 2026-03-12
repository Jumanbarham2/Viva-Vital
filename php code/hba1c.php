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
    $required = ['user_id', 'hba1c_percent'];
    foreach ($required as $field) {
        if (!isset($input[$field])) {
            throw new Exception("Missing required field: $field");
        }
    }

    $hba1c = (float)$input['hba1c_percent'];
    if ($hba1c < 3.0 || $hba1c > 20.0) {
        throw new Exception("HbA1c value must be between 4.0 and 14.0");
    }

    $stmt = $con->prepare("SELECT ID FROM user_account WHERE ID = ?");
    $stmt->bind_param("i", $input['user_id']);
    $stmt->execute();
    $result = $stmt->get_result();

    if ($result->num_rows === 0) {
        throw new Exception("Invalid user ID");
    }

    // Insert HbA1c record
    $insert = $con->prepare("INSERT INTO hba1c 
        (user_id, hba1c_percent) 
        VALUES (?, ?)");
    
    $insert->bind_param("id", 
        $input['user_id'],
        $hba1c
    );

    if (!$insert->execute()) {
        throw new Exception("Failed to save HbA1c record: " . $insert->error);
    }

    $response = [
        'success' => true,
        'error' => false,
        'hba1c_id' => $insert->insert_id,
        'hba1c_percent' => $hba1c,
        'test_date' => date('Y-m-d H:i:s')
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