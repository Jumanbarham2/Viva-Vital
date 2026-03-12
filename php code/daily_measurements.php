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

    // Validate required fields (blood_glucose is now optional)
    $required = ['user_id', 'bp_systolic', 'bp_diastolic', 'measurement_time'];
    foreach ($required as $field) {
        if (!isset($input[$field])) {
            throw new Exception("Missing required field: $field");
        }
    }

    // Validate blood pressure values
    $bp_systolic = (int)$input['bp_systolic'];
    $bp_diastolic = (int)$input['bp_diastolic'];
    if ($bp_systolic < 50 || $bp_systolic > 300) {
        throw new Exception("Systolic blood pressure must be between 50 and 300");
    }
    if ($bp_diastolic < 30 || $bp_diastolic > 200) {
        throw new Exception("Diastolic blood pressure must be between 30 and 200");
    }
    if ($bp_systolic < $bp_diastolic) {
        throw new Exception("Systolic pressure must be higher than diastolic");
    }

    // Handle optional blood glucose
    $blood_glucose = isset($input['blood_glucose']) ? (int)$input['blood_glucose'] : null;
    if ($blood_glucose !== null && ($blood_glucose < 20 || $blood_glucose > 600)) {
        throw new Exception("Blood glucose must be between 20 and 600 mg/dL or null");
    }

    // Validate measurement time
    $valid_times = ['Before meal', 'After meal', 'Fasting', 'None...'];
    if (!in_array($input['measurement_time'], $valid_times)) {
        throw new Exception("Invalid measurement time");
    }

    // Check user exists
    $stmt = $con->prepare("SELECT ID FROM user_account WHERE ID = ?");
    $stmt->bind_param("i", $input['user_id']);
    $stmt->execute();
    $result = $stmt->get_result();

    if ($result->num_rows === 0) {
        throw new Exception("Invalid user ID");
    }

    // Insert daily measurement record
    $insert = $con->prepare("INSERT INTO daily_measurements 
        (user_id, bp_systolic, bp_diastolic, blood_glucose, measurement_time, measurement_date) 
        VALUES (?, ?, ?, ?, ?, NOW())");
    
    $insert->bind_param("iiiss", 
        $input['user_id'],
        $bp_systolic,
        $bp_diastolic,
        $blood_glucose,
        $input['measurement_time']
    );

    if (!$insert->execute()) {
        throw new Exception("Failed to save daily measurement record: " . $insert->error);
    }

    $response = [
        'success' => true,
        'error' => false,
        'measurement_id' => $insert->insert_id,
        'bp_systolic' => $bp_systolic,
        'bp_diastolic' => $bp_diastolic,
        'blood_glucose' => $blood_glucose,
        'measurement_time' => $input['measurement_time'],
        'measurement_date' => date('Y-m-d H:i:s')
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