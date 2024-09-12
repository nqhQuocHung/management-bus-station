<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Bus Stations</title>
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
        .add-station-btn {
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
<h1 align="center">Bus Stations</h1>
<a href="<c:url value="/admin/addstation"/>" class="add-station-btn">+ Add new</a>
<table>
    <thead>
    <tr>
        <th>ID</th>
        <th>Address</th>
        <th>Map</th>
        <th>Actions</th>
    </tr>
    </thead>
    <tbody id="stationTable">
    </tbody>
</table>
<script>
    $(document).ready(function() {
        loadStations();

        function loadStations() {
            axios.get('http://localhost:8080/busstation/api/v1/stations/list')
                .then(function(response) {
                    console.log("Data received:", response.data);
                    var data = response.data;
                    var tableContent = '';
                    data.forEach(function(station, index) {
                        var rowClass = index % 2 === 0 ? 'even' : 'odd';
                        tableContent += '<tr class="' + rowClass + '">';
                        tableContent += '<td>' + station.id + '</td>';
                        tableContent += '<td>' + station.address + '</td>';
                        tableContent += '<td><a href="' + station.mapUrl + '" target="_blank">View Map</a></td>';
                        tableContent += '<td>' +
                            '<a href="http://localhost:8080/busstation/admin/station_detail?id=' + station.id + '">Edit</a> ' +
                            '<button class="delete-btn" data-id="' + station.id + '">Delete</button>' +
                            '</td>';
                        tableContent += '</tr>';
                    });
                    $('#stationTable').html(tableContent);

                    $('.delete-btn').on('click', function() {
                        var stationId = $(this).data('id');
                        if (confirm('Are you sure you want to delete this station?')) {
                            deleteStation(stationId);
                        }
                    });
                })
                .catch(function(error) {
                    console.error("Failed to load data:", error);
                });
        }

        function deleteStation(stationId) {
            axios.delete('http://localhost:8080/busstation/api/v1/stations/delete/' + stationId)
                .then(function() {
                    loadStations();
                })
                .catch(function(error) {
                    console.error("Failed to delete station:", error);
                });
        }
    });
</script>
</body>
</html>
