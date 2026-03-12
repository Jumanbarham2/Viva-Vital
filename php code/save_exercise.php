<?php
header('Content-Type: application/json');
ini_set('display_errors', 1);
error_reporting(E_ALL);

// Database configuration
$db_host = "localhost";
$db_user = "root";
$db_pass = "";
$db_name = "viva";

// Create response array
$response = [
    'success' => false,
    'message' => '',
    'reminder_id' => null
];

try {
    // Create database connection
    $con = new mysqli($db_host, $db_user, $db_pass, $db_name);
    
    if ($con->connect_error) {
        throw new Exception("Database connection failed: " . $con->connect_error);
    }

    // Get and validate JSON input
    $jsonInput = file_get_contents('php://input');
    if (empty($jsonInput)) {
        throw new Exception("No input data received");
    }

    $input = json_decode($jsonInput, true);
    if (json_last_error() !== JSON_ERROR_NONE) {
        throw new Exception("Invalid JSON: " . json_last_error_msg());
    }

    // Validate required fields
    $requiredFields = ['user_id', 'exercise_type', 'days', 'reminder_time'];
    foreach ($requiredFields as $field) {
        if (!isset($input[$field]) || empty($input[$field])) {
            throw new Exception("Missing required field: " . $field);
        }
    }

    // Validate user exists
    $userId = (int)$input['user_id'];
    $stmt = $con->prepare("SELECT ID FROM user_account WHERE ID = ?");
    $stmt->bind_param("i", $userId);
    $stmt->execute();
    $result = $stmt->get_result();

    if ($result->num_rows === 0) {
        throw new Exception("User with ID $userId does not exist");
    }

    // Validate and format time (HH:MM:SS)
    $time = trim($input['reminder_time']);
    if (!preg_match('/^([01]?[0-9]|2[0-3]):[0-5][0-9](:[0-5][0-9])?$/', $time)) {
        throw new Exception("Invalid time format. Expected HH:MM or HH:MM:SS");
    }
    
    // Ensure time has seconds
    if (substr_count($time, ':') === 1) {
        $time .= ':00';
    }

    // Process days selection (from MultiSpinner)
    $daysString = '';
    if (is_array($input['days'])) {
        // Remove empty values and validate each day
        $validDays = ['Everyday', 'Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday'];
        $filteredDays = array_filter($input['days'], function($day) use ($validDays) {
            return in_array($day, $validDays);
        });
        
        if (empty($filteredDays)) {
            throw new Exception("No valid days selected");
        }
        
        $daysString = implode(",", $filteredDays);
    } else {
        // Handle case where days comes as string
        $daysString = trim($input['days']);
        if (empty($daysString)) {
            throw new Exception("No days selected");
        }
    }

    // Validate days string length
    if (strlen($daysString) > 100) {
        throw new Exception("Days selection too long (max 100 characters)");
    }

    // Insert exercise reminder
    $stmt = $con->prepare("INSERT INTO exercise_reminder 
        (user_id, exercise_type, days, reminder_time) 
        VALUES (?, ?, ?, ?)");
    
    $stmt->bind_param("isss", 
        $userId,
        $input['exercise_type'],
        $daysString,
        $time
    );

    if (!$stmt->execute()) {
        throw new Exception("Database error: " . $stmt->error);
    }

    // Success response
    $response['success'] = true;
    $response['message'] = 'Exercise reminder saved successfully';
    $response['reminder_id'] = $stmt->insert_id;

} catch (Exception $e) {
    http_response_code(400);
    $response['message'] = $e->getMessage();
    error_log("Exercise Reminder Error: " . $e->getMessage());
} finally {
    if (isset($con)) {
        $con->close();
    }
    echo json_encode($response);
    exit;
}
?>