<?php
header('Content-Type: application/json');

// Database configuration
$nameServer   = "localhost";
$userName     = "root";
$password     = "";
$nameDatabase = "viva";

// Create connection
$con = new mysqli($nameServer, $userName, $password, $nameDatabase);

// Check connection
if ($con->connect_error) {
    die(json_encode([
        "error" => true,
        "message" => "Database connection failed: " . $con->connect_error
    ]));
}

// Get input
$username = $_POST["username"] ?? '';
$password = $_POST["password"] ?? '';

// Validate input
if (empty($username) || empty($password)) {
    echo json_encode([
        "error" => true,
        "message" => "Username and password are required"
    ]);
    exit;
}

// Prepare SQL statement to prevent SQL injection
$stmt = $con->prepare("SELECT id, username, password FROM user_account WHERE username = ?");
$stmt->bind_param("s", $username);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows === 1) {
    $user = $result->fetch_assoc();
    
    // Verify the password against the stored bcrypt hash
    if (password_verify($password, $user['password'])) {
        // Login successful
        echo json_encode([
            "error" => false,
            "message" => "Login successful",
            "user" => [
                "id" => $user['id'],
                "username" => $user['username']
            ]
        ]);
    } else {
        // Invalid password
        echo json_encode([
            "error" => true,
            "message" => "Invalid credentials"
        ]);
    }
} else {
    // User not found
    echo json_encode([
        "error" => true,
        "message" => "Invalid credentials"
    ]);
}

// Close connections
$stmt->close();
$con->close();
?>