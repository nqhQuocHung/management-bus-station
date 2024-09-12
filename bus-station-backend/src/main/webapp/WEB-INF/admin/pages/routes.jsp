<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Routes</title>
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/axios/dist/axios.min.js"></script>
    <style>
        table {
            width: 100%;
            border-collapse: collapse;
        }
        table, th, td {
            border: 1px solid black;
        }
        th, td {
            padding: 8px;
            text-align: left;
        }
        th {
            background-color: #469A9E;
        }
        .add-route-btn {
            display: inline-block;
            margin-bottom: 10px;
            padding: 10px 20px;
            background-color: #4CAF50;
            color: white;
            text-decoration: none;
            border-radius: 5px;
            position: fixed;
            top: 60px;
            right: 18px;
        }
        .even {
            background-color: #f2f2f2;
        }
        .odd {
            background-color: #ffffff;
        }
    </style>
</head>
<body>
<h1 align="center">Routes</h1>
<a href="<c:url value="/admin/route"/>" class="add-route-btn"> +Add new</a>
<table>
    <thead>
    <tr>
        <th>ID</th>
        <th>Name</th>
        <th>From Station</th>
        <th>To Station</th>
        <th>Seat Price</th>
        <th>Cargo Price</th>
        <th>Actions</th>
    </tr>
    </thead>
    <tbody id="routeTable">
    <!-- Content will be loaded via AJAX -->
    </tbody>
</table>
<script>
    $(document).ready(function() {
        loadRoutes();

        function loadRoutes() {
            axios.get('http://localhost:8080/busstation/api/v1/route/list')
                .then(function(response) {
                    console.log("Data received:", response.data);
                    var data = response.data.results; // Sử dụng data.results thay vì data
                    var tableContent = '';
                    data.forEach(function(route, index) {
                        var rowClass = index % 2 === 0 ? 'even' : 'odd';
                        tableContent += '<tr class="' + rowClass + '">';
                        tableContent += '<td>' + route.id + '</td>';
                        tableContent += '<td>' + route.name + '</td>';
                        tableContent += '<td>' + route.fromStation.name + '</td>';
                        tableContent += '<td>' + route.toStation.name + '</td>';
                        tableContent += '<td>' + route.seatPrice + '</td>';
                        tableContent += '<td>' + route.cargoPrice + '</td>';
                        tableContent += '<td>' +
                            '<a href="http://localhost:8080/busstation/admin/route?id=' + route.id + '">Edit</a> ' +
                            '<button class="delete-btn" data-id="' + route.id + '">Delete</button>' +
                            '</td>';
                        tableContent += '</tr>';
                    });
                    $('#routeTable').html(tableContent);

                    $('.delete-btn').on('click', function() {
                        var routeId = $(this).data('id');
                        if (confirm('Are you sure you want to delete this route?')) {
                            deleteRoute(routeId);
                        }
                    });
                })
                .catch(function(error) {
                    console.error("Failed to load data:", error);
                });
        }

        function deleteRoute(routeId) {
            axios.delete('http://localhost:8080/busstation/api/v1/route/' + routeId)
                .then(function() {
                    loadRoutes();
                })
                .catch(function(error) {
                    console.error("Failed to delete route:", error);
                });
        }
    });
</script>
</body>
</html>
