<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Unverified Companies</title>
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
        .add-company-btn {
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
<h1 align="center">Unverified Transportation Companies</h1>
<a href="<c:url value="/admin/company"/>" class="add-company-btn"> +Add new</a>
<table>
    <thead>
    <tr>
        <th>ID</th>
        <th>Name</th>
        <th>Phone</th>
        <th>Email</th>
        <th>Actions</th>
    </tr>
    </thead>
    <tbody id="companyTable">
    <!-- Content will be loaded via AJAX -->
    </tbody>
</table>
<script>
    $(document).ready(function() {
        loadCompanies();

        function loadCompanies() {
            axios.get('http://localhost:8080/busstation/api/v1/transportation_company/unverified')
                .then(function(response) {
                    console.log("Data received:", response.data);
                    var data = response.data;
                    var tableContent = '';
                    data.forEach(function(company, index) {
                        var rowClass = index % 2 === 0 ? 'even' : 'odd';
                        tableContent += '<tr class="' + rowClass + '">';
                        tableContent += '<td>' + company.id + '</td>';
                        tableContent += '<td>' + company.name + '</td>';
                        tableContent += '<td>' + company.phone + '</td>';
                        tableContent += '<td>' + company.email + '</td>';
                        tableContent += '<td>' +
                            '<button class="verify-btn" data-id="' + company.id + '">Verify</button> ' +
                            '</td>';
                        tableContent += '</tr>';
                    });
                    $('#companyTable').html(tableContent);

                    $('.verify-btn').on('click', function() {
                        var companyId = $(this).data('id');
                        if (confirm('Are you sure you want to verify this company?')) {
                            verifyCompany(companyId);
                        }
                    });

                    $('.delete-btn').on('click', function() {
                        var companyId = $(this).data('id');
                        if (confirm('Are you sure you want to delete this company?')) {
                            deleteCompany(companyId);
                        }
                    });
                })
                .catch(function(error) {
                    console.error("Failed to load data:", error);
                });
        }

        function verifyCompany(companyId) {
            axios.put('http://localhost:8080/busstation/api/v1/transportation_company/verify/' + companyId)
                .then(function() {
                    loadCompanies();
                })
                .catch(function(error) {
                    console.error("Failed to verify company:", error);
                });
        }

    });
</script>
</body>
</html>
