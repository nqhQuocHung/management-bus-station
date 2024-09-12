<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Create New Bus Station</title>
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/axios/dist/axios.min.js"></script>
    <style>
        .form-container {
            max-width: 600px;
            margin: auto;
            padding: 20px;
            border: 1px solid #ccc;
            border-radius: 10px;
            background-color: #f9f9f9;
        }
        .form-container h2 {
            text-align: center;
        }
        .form-container label {
            display: block;
            margin-bottom: 8px;
        }
        .form-container input[type="text"], .form-container input[type="url"] {
            width: 100%;
            padding: 8px;
            margin-bottom: 10px;
            border: 1px solid #ccc;
            border-radius: 5px;
        }
        .form-container button {
            display: inline-block;
            padding: 10px 20px;
            margin-right: 10px;
            border: none;
            border-radius: 5px;
            background-color: #4CAF50;
            color: white;
            cursor: pointer;
        }
        .form-container button.cancel {
            background-color: #f44336;
        }
    </style>
</head>
<body>
<div class="form-container">
    <h2>Create New Bus Station</h2>
    <form id="newStationForm">
        <label for="address">Address:</label>
        <input type="text" id="address" name="address" required>

        <label for="mapUrl">Map URL:</label>
        <input type="url" id="mapUrl" name="mapUrl" required>

        <button type="button" id="saveBtn">Save</button>
        <a class="btn btn-primary" href="<c:url value="/admin/stations"/>>">Cancel</a>

    </form>
</div>

<script>
    $(document).ready(function() {
        $('#saveBtn').on('click', function() {
            const newStation = {
                address: $('#address').val(),
                mapUrl: $('#mapUrl').val()
            };

            axios.post('http://localhost:8080/busstation/api/v1/stations/add', newStation)
                .then(function() {
                    window.location.href = 'http://localhost:8080/busstation/admin/stations';
                })
                .catch(function(error) {
                    console.error("Failed to create new station:", error);
                });
        });
    });
</script>
</body>
</html>
