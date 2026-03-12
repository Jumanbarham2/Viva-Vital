<?php
header('Content-Type: application/json');
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

    $json_input = file_get_contents('php://input');
    $input = json_decode($json_input, true);
    
    if ($input === null) {
        throw new Exception("Invalid JSON input: " . json_last_error_msg() . " | Raw input: " . $json_input);
    }

    // Log received input for debugging
    file_put_contents('medication_debug.log', date('Y-m-d H:i:s') . " - Input: " . print_r($input, true) . "\n", FILE_APPEND);

    // Required fields with validation
    $required = [
        'user_id' => 'User ID',
        'name' => 'Medication Name',
        'days_of_week' => 'Days of Week',
        'times_per_day' => 'Times Per Day',
        'dosage_per_intake' => 'Dosage Per Intake'
    ];

    foreach ($required as $field => $name) {
        if (!isset($input[$field]) {
            throw new Exception("$name is required");
        }
        if ($field !== 'days_of_week' && empty($input[$field])) {
            throw new Exception("$name cannot be empty");
        }
    }

    // Convert and validate numeric fields
    $user_id = (int)$input['user_id'];
    $times_per_day = (int)$input['times_per_day'];
    $dosage_per_intake = (int)$input['dosage_per_intake'];
    $remaining_pills = isset($input['remaining_pills']) ? (int)$input['remaining_pills'] : 0;

    if ($user_id <= 0) {
        throw new Exception("Invalid User ID");
    }
    if ($times_per_day < 1 || $times_per_day > 4) {
        throw new Exception("Times per day must be between 1 and 4");
    }
    if ($dosage_per_intake < 1 || $dosage_per_intake > 4) {
        throw new Exception("Dosage per intake must be between 1 and 4");
    }
    if ($remaining_pills < 0 || $remaining_pills > 999) {
        throw new Exception("Remaining pills must be between 0-999");
    }

    // Validate days of week
    $valid_days = ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday', 'Everyday'];
    $days_array = array_map('trim', explode(',', $input['days_of_week']));
    $days_array = array_map('ucfirst', $days_array); // Ensure proper capitalization
    
    foreach ($days_array as $day) {
        if (!in_array($day, $valid_days)) {
            throw new Exception("Invalid day: $day. Valid days are: " . implode(', ', $valid_days));
        }
    }
    $days_normalized = implode(',', $days_array);

    // Verify user exists
    $stmt = $con->prepare("SELECT ID FROM user_account WHERE ID = ?");
    $stmt->bind_param("i", $user_id);
    $stmt->execute();
    
    if ($stmt->get_result()->num_rows === 0) {
        throw new Exception("User with ID $user_id not found");
    }

    // Handle optional fields
    $description = isset($input['description']) ? $input['description'] : '';
    $notes = isset($input['notes']) ? $input['notes'] : '';

    // Insert medication
    $insertMed = $con->prepare("INSERT INTO medications 
        (user_id, name, description, days_of_week, times_per_day, dosage_per_intake, notes, remaining_pills) 
        VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
    
    $insertMed->bind_param("isssiisi", 
        $user_id,
        $input['name'],
        $description,
        $days_normalized,
        $times_per_day,
        $dosage_per_intake,
        $notes,
        $remaining_pills
    );

    if (!$insertMed->execute()) {
        throw new Exception("Database error: " . $con->error);
    }

    $response = [
        'success' => true,
        'medication_id' => $insertMed->insert_id,
        'message' => 'Medication saved successfully'
    ];

} catch (Exception $e) {
    http_response_code(400);
    $response = [
        'success' => false,
        'message' => $e->getMessage()
    ];
    file_put_contents('medication_errors.log', date('Y-m-d H:i:s') . " - Error: " . $e->getMessage() . "\n", FILE_APPEND);
} finally {
    if (isset($con)) $con->close();
    echo json_encode($response);
}
?>