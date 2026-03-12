<?php
header('Content-Type: application/json');
$con = new mysqli("localhost", "root", "", "viva");

if ($con->connect_error) {
    die(json_encode(["error" => true, "message" => "Database connection failed"]));
}

$email = $_POST['email'] ?? '';

$stmt = $con->prepare("SELECT ID FROM user_account WHERE email = ?");
$stmt->bind_param("s", $email);
$stmt->execute();

if ($stmt->get_result()->num_rows === 0) {
    echo json_encode(["error" => false, "message" => "If this email exists, you'll receive a reset link"]);
    exit;
}

$success = mail($email, "Password Reset", "Click here to reset your password...");

echo json_encode([
    "error" => !$success,
    "message" => $success ? "Reset email sent" : "Failed to send email"
]);
?>