<?php
header('Content-Type: application/json');
ini_set('display_errors', 1); 
error_reporting(E_ALL); 

$db_host = "localhost";
$db_user = "root";
$db_pass = "";
$db_name = "viva";

$conn = new mysqli($db_host, $db_user, $db_pass, $db_name);

if ($conn->connect_error) {
    die(json_encode([
        'error' => true,
        'message' => 'Connection failed: ' . $conn->connect_error,
        'server' => $_SERVER 
    ]));
}

$input = json_decode(file_get_contents('php://input'), true);

error_log("Raw input: " . print_r($input, true));

if (empty($input['feeling']) || !isset($input['symptoms'])) {
    http_response_code(400);
    die(json_encode(['error' => true, 'message' => 'Feeling and symptoms required']));
}

$physical = !empty($input['physical']) ? 1 : 0;
$stress = !empty($input['stress']) ? 1 : 0;
$comment = !empty($input['comment']) ? $conn->real_escape_string($input['comment']) : NULL;

try {
    $stmt = $conn->prepare("INSERT INTO daily_status 
        (feeling, symptoms, physical, stress, comment) 
        VALUES (?, ?, ?, ?, ?)");
    
    if (!$stmt) {
        throw new Exception("Prepare failed: " . $conn->error);
    }

    $bind = $stmt->bind_param("ssiis", 
        $input['feeling'],
        $input['symptoms'],
        $physical,
        $stress,
        $comment
    );
    
    if (!$bind) {
        throw new Exception("Bind failed: " . $stmt->error);
    }

    if ($stmt->execute()) {
        echo json_encode([
            'error' => false,
            'id' => $stmt->insert_id
        ]);
    } else {
        throw new Exception("Execute failed: " . $stmt->error);
    }
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'error' => true,
        'message' => $e->getMessage(),
        'trace' => $e->getTraceAsString() // Debug
    ]);
} finally {
    $conn->close();
}
?>