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

    $method = $_SERVER['REQUEST_METHOD'];
    $input = json_decode(file_get_contents('php://input'), true);

    // Route the request
    switch ($method) {
        case 'POST':
            handlePostRequest($con, $input);
            break;
        case 'GET':
            handleGetRequest($con, $_GET);
            break;
        default:
            throw new Exception("Method not allowed", 405);
    }

} catch (Exception $e) {
    http_response_code($e->getCode() ?: 400);
    echo json_encode([
        'success' => false,
        'error' => true,
        'message' => $e->getMessage()
    ]);
} finally {
    if (isset($con)) $con->close();
}

function handlePostRequest($con, $input) {
    // Validate input
    if (json_last_error() !== JSON_ERROR_NONE) {
        throw new Exception("Invalid JSON input");
    }

    $required = ['user_id', 'bp_systolic', 'bp_diastolic', 'heart_rate', 
                'oxygen_saturation', 'blood_glucose', 'respiratory_rate', 
                'weight', 'measurement_date'];
    
    foreach ($required as $field) {
        if (!isset($input[$field])) {
            throw new Exception("Missing required field: $field");
        }
    }

    // Validate user exists
    validateUser($con, $input['user_id']);

    // Validate measurement values
    validateMeasurementValues($input);

    $stmt = $con->prepare("INSERT INTO vital_measurements 
        (user_id, bp_systolic, bp_diastolic, heart_rate, oxygen_saturation, 
         blood_glucose, respiratory_rate, weight, measurement_date) 
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
    
    $stmt->bind_param("iiiiiiids", 
        $input['user_id'],
        $input['bp_systolic'],
        $input['bp_diastolic'],
        $input['heart_rate'],
        $input['oxygen_saturation'],
        $input['blood_glucose'],
        $input['respiratory_rate'],
        $input['weight'],
        $input['measurement_date']
    );

    if (!$stmt->execute()) {
        throw new Exception("Failed to save vital measurements: " . $stmt->error);
    }

    echo json_encode([
        'success' => true,
        'error' => false,
        'vital_id' => $stmt->insert_id,
        'measurement_date' => $input['measurement_date']
    ]);
}

function handleGetRequest($con, $params) {
    // Validate required parameters
    if (!isset($params['user_id'])) {
        throw new Exception("user_id parameter is required");
    }

    $user_id = (int)$params['user_id'];
    validateUser($con, $user_id);

    // Build query based on optional parameters
    $query = "SELECT * FROM vital_measurements WHERE user_id = ?";
    $types = "i";
    $values = [$user_id];

    // Optional date range filter
    if (isset($params['start_date']) && isset($params['end_date'])) {
        $query .= " AND measurement_date BETWEEN ? AND ?";
        $types .= "ss";
        array_push($values, $params['start_date'], $params['end_date']);
    } elseif (isset($params['start_date'])) {
        $query .= " AND measurement_date >= ?";
        $types .= "s";
        array_push($values, $params['start_date']);
    } elseif (isset($params['end_date'])) {
        $query .= " AND measurement_date <= ?";
        $types .= "s";
        array_push($values, $params['end_date']);
    }

    // Optional limit
    if (isset($params['limit'])) {
        $limit = (int)$params['limit'];
        if ($limit > 0) {
            $query .= " LIMIT ?";
            $types .= "i";
            array_push($values, $limit);
        }
    }
    $stmt = $con->prepare($query);
    $stmt->bind_param($types, ...$values);
    $stmt->execute();
    $result = $stmt->get_result();

    $measurements = [];
    while ($row = $result->fetch_assoc()) {
        $measurements[] = [
            'id' => $row['id'],
            'bp_systolic' => $row['bp_systolic'],
            'bp_diastolic' => $row['bp_diastolic'],
            'heart_rate' => $row['heart_rate'],
            'oxygen_saturation' => $row['oxygen_saturation'],
            'blood_glucose' => $row['blood_glucose'],
            'respiratory_rate' => $row['respiratory_rate'],
            'weight' => (float)$row['weight'],
            'measurement_date' => $row['measurement_date'],
            'created_at' => $row['created_at']
        ];
    }

    echo json_encode([
        'success' => true,
        'error' => false,
        'count' => count($measurements),
        'measurements' => $measurements
    ]);
}

function validateUser($con, $user_id) {
    $stmt = $con->prepare("SELECT ID FROM user_account WHERE ID = ?");
    $stmt->bind_param("i", $user_id);
    $stmt->execute();
    $result = $stmt->get_result();

    if ($result->num_rows === 0) {
        throw new Exception("Invalid user ID");
    }
}
//validation of ranges 
function validateMeasurementValues($input) {
    $validations = [
        'bp_systolic' => ['min' => 50, 'max' => 250],
        'bp_diastolic' => ['min' => 30, 'max' => 150],
        'heart_rate' => ['min' => 30, 'max' => 190],
        'oxygen_saturation' => ['min' => 0, 'max' => 100],
        'blood_glucose' => ['min' => 40],
        'respiratory_rate' => ['min' => 6, 'max' => 60],
        'weight' => ['min' => 30.0, 'max' => 300.0]
    ];

    foreach ($validations as $field => $range) {
        $value = $field === 'weight' ? (float)$input[$field] : (int)$input[$field];
        
        if (isset($range['min']) && $value < $range['min']) {
            throw new Exception("$field must be at least {$range['min']}");
        }
        
        if (isset($range['max']) && $value > $range['max']) {
            throw new Exception("$field must be at most {$range['max']}");
        }
    }

    // Special validation: systolic > diastolic
    if ((int)$input['bp_systolic'] <= (int)$input['bp_diastolic']) {
        throw new Exception("Systolic blood pressure must be greater than diastolic");
    }

    // Validate date format
    if (!DateTime::createFromFormat('Y-m-d H:i:s', $input['measurement_date'])) {
        throw new Exception("Invalid measurement_date format. Expected YYYY-MM-DD HH:MM:SS");
    }
}
?>