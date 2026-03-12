<?php
header('Content-Type: application/json');

// Database config
$db_host = "localhost";
$db_user = "root";
$db_pass = "";
$db_name = "viva";

$conn = new mysqli($db_host, $db_user, $db_pass, $db_name);

if ($conn->connect_error) {
    die(json_encode(['error' => true, 'messages' => ['Database connection failed']]));
}

// Get JSON input
$json = file_get_contents('php://input');
$data = json_decode($json, true);

// Input validation
$errors = [];
$username = $data['username'] ?? '';
$email = $data['email'] ?? '';
$password = $data['password'] ?? '';
$confirm_password = $data['confirm_password'] ?? '';

if (empty($username)) $errors[] = "Username required";
if (empty($email)) $errors[] = "Email required";
if (empty($password)) $errors[] = "Password required";
if ($password !== $confirm_password) $errors[] = "Passwords don't match";
if (!filter_var($email, FILTER_VALIDATE_EMAIL)) $errors[] = "Invalid email format";
if (strlen($password) < 8) $errors[] = "Password must be at least 8 characters";

if (!empty($errors)) {
    echo json_encode(['error' => true, 'messages' => $errors]);
    exit;
}

// Check existing user
$stmt = $conn->prepare("SELECT ID FROM user_account WHERE Username = ? OR email = ?");
$stmt->bind_param("ss", $username, $email);
$stmt->execute();

if ($stmt->get_result()->num_rows > 0) {
    echo json_encode(['error' => true, 'messages' => ['Username or email already exists']]);
    exit;
}

// Hash password
$hashedPassword = password_hash($password, PASSWORD_BCRYPT);

// Insert user
$stmt = $conn->prepare("INSERT INTO user_account (Username, email, password) VALUES (?, ?, ?)");
$stmt->bind_param("sss", $username, $email, $hashedPassword);

if ($stmt->execute()) {
    $response = [
        'error' => false,
        'message' => 'Registration successful.'
    ];
} else {
    $response = [
        'error' => true,
        'messages' => ['Registration failed. Please try again. Error: ' . $conn->error]
    ];
}

echo json_encode($response);
$stmt->close();
$conn->close();
?>