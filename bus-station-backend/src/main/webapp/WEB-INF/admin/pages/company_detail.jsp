<!DOCTYPE html>
<html>
<head>
    <title>Update Company Information</title>
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
        .form-container input[type="text"], .form-container input[type="email"],
        .form-container input[type="file"], .form-container select {
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
        #avatarPreview {
            max-width: 150px;
            height: auto;
            display: none;
            margin-top: 10px;
        }
    </style>
</head>
<body>
<div class="form-container">
    <h2>Update Company Information</h2>
    <form id="updateCompanyForm" enctype="multipart/form-data">
        <label for="name">Name:</label>
        <input type="text" id="name" name="name" required>

        <label for="phone">Phone:</label>
        <input type="text" id="phone" name="phone" required>

        <label for="email">Email:</label>
        <input type="email" id="email" name="email" required>

        <label for="isVerified">Is Verified:</label>
        <input type="checkbox" id="isVerified" name="isVerified">

        <label for="isActive">Is Active:</label>
        <input type="checkbox" id="isActive" name="isActive">

        <label for="isCargoTransport">Is Cargo Transport:</label>
        <input type="checkbox" id="isCargoTransport" name="isCargoTransport">

        <label for="avatar">Avatar:</label>
        <input type="file" id="avatar" name="avatar" accept="image/*">
        <img id="avatarPreview" src="#" alt="Avatar Preview">

        <label for="managerId">Manager ID:</label>
        <select id="managerId" name="managerId" required>
            <option value="">Select a manager</option>
        </select>

        <button type="button" id="saveBtn">Save</button>
        <button type="button" class="cancel" onclick="window.location.href='http://localhost:8080/busstation/admin/companies'">Cancel</button>
    </form>
</div>

<script>
    $(document).ready(function() {
        const companyId = new URLSearchParams(window.location.search).get('id');


        axios.get('http://localhost:8080/busstation/api/v1/transportation_company/get/' + companyId)
            .then(function(response) {
                const company = response.data;
                $('#name').val(company.name);
                $('#phone').val(company.phone);
                $('#email').val(company.email);
                $('#isVerified').prop('checked', company.isVerified);
                $('#isActive').prop('checked', company.isActive);
                $('#isCargoTransport').prop('checked', company.isCargoTransport);
                loadManagers(company.managerId);
            })
            .catch(function(error) {
                console.error("Failed to load company data:", error);
                alert("Failed to load company data. Please check the ID and try again.");
            });

        function loadManagers(selectedManagerId) {
            axios.get('http://localhost:8080/busstation/admin/users/role/3')
                .then(function(response) {
                    const managers = response.data;
                    const managerSelect = $('#managerId');
                    managers.forEach(manager => {
                        var option = new Option(manager.id + ' - ' + manager.firstname + ' ' + manager.lastname, manager.id);
                        managerSelect.append(option);
                        if (manager.id.toString() === selectedManagerId.toString()) {
                            $(option).prop('selected', true);
                        }
                    });
                })
                .catch(function(error) {
                    console.error('Failed to load managers:', error);
                });
        }

        // Function to show image preview
        function showImagePreview(fileInput, imageElement) {
            if (fileInput.files && fileInput.files[0]) {
                var reader = new FileReader();

                reader.onload = function (e) {
                    $(imageElement).attr('src', e.target.result);
                    $(imageElement).show();
                };

                reader.readAsDataURL(fileInput.files[0]);
            } else {
                $(imageElement).hide();
            }
        }

        $('#avatar').change(function() {
            showImagePreview(this, '#avatarPreview');
        });

        $('#saveBtn').on('click', function() {
            const formData = new FormData(document.getElementById('updateCompanyForm'));
            formData.set('isVerified', $('#isVerified').is(':checked'));
            formData.set('isActive', $('#isActive').is(':checked'));
            formData.set('isCargoTransport', $('#isCargoTransport').is(':checked'));

            axios.put('http://localhost:8080/busstation/api/v1/transportation_company/' + companyId, formData, {
                headers: {
                    'Content-Type': 'multipart/form-data'
                }
            })
                .then(function(response) {
                    alert('Company updated successfully.');
                    window.location.href = 'http://localhost:8080/busstation/admin/companies';
                })
                .catch(function(error) {
                    console.error("Failed to update company:", error);
                    alert("Failed to update company. Please try again.");
                });
        });
    });
</script>
</body>
</html>
