const newUserTab = $('a[id="new-user"]');
const defaultForm = $('#defaultForm');

$('document').ready(async function () {
    await getAllUsers();
    newUserTab.on('show.bs.tab', async () => {
        let addButton = `<input type="submit" class="btn btn-success mb-3" value="Add new user">`;
        defaultForm.find('#formBody').append(addButton);

        await fetch('api/v1/users/role-list').then(res => {
            res.json().then(roles => {
                $.each(roles, function (j, role) {
                    let roleOption = `<option value="${role.role}">${role.role.replaceAll('ROLE_', '')}</option>`
                    $('#defaultRoles').append(roleOption)
                })
            })
        });

        $('#idFormGroup').attr('style', 'display:none');

        await addNewUser();
    })
    newUserTab.on('hidden.bs.tab', () => {
        defaultForm.attr('class', 'w-100').find('#modalFooter').attr({
            'class': 'modal-footer',
            'style': ''
        });

        $('input[class="btn btn-success mb-3"]').remove();

        $('#idFormGroup').attr('style', '');

        defaultForm[0].reset();

        $('#modalBody').append(defaultForm);
    })
});

async function getAllUsers() {
    await $.ajax({
        type: 'GET',
        url: '/api/v1/users',
        success: function (users) {
            let table = $('#usersTab tbody');

            $.each(users, function (i, user) {
                tableFilling(table, user);
            });
        }
    });
}

async function clickUserButton(buttonUserId, buttonAction) {
    let defaultModal = $('#defaultModal');

    await openInModalUser(`api/v1/users/${buttonUserId}`, buttonAction)

    defaultModal.modal('show');

    defaultForm.one('submit', async (e) => {
        e.preventDefault();

        let body = {
            id: $('#defaultId').val(),
            firstName: $('#defaultFirstName').val(),
            lastName: $('#defaultLastName').val(),
            email: $('#defaultEmail').val(),
            password: $('#defaultPassword').val(),
            roles: $('#defaultRoles').val()
        }

        if (buttonAction === 'edit') {
            await fetch('/api/v1/users/update', {
                method: 'POST',
                headers: {'Content-type': 'application/json'},
                body: JSON.stringify(body)
            })

        } else if (buttonAction === 'delete') {
            await fetch('/api/v1/users/delete', {
                method: 'DELETE',
                headers: {'Content-type': 'application/json'},
                body: JSON.stringify(body)
            });
        }

        defaultModal.modal('hide');

        await updateTable(buttonAction, buttonUserId);
    })

    defaultModal.on('hidden.bs.modal', async function () {
        document.getElementById('defaultModalSubmit').innerHTML = 'Edit'
        defaultForm.find('input').not('#defaultId').prop('readonly', false);

        $('#defaultRolesLab').attr({
            'class': 'my-0 show',
            'style': 'font-weight: bold'
        })
        $('#defaultRolesDeleteLab').attr({
            'class': 'my-0 fade',
            'style': 'font-weight: bold; display: none'
        })
        $('#defaultRoles').attr({
            'class': 'form-control show',
            'style': 'height: 55px'
        }).children().remove()
        $('#defaultRolesDelete').attr({
            'class': 'form-control fade',
            'style': 'height: 30px; display: none'
        })
        defaultForm[0].reset()
    })
}

async function openInModalUser(url, operation) {
    let user = await fetch(url);


    if(operation === 'edit') {
        user.json().then(us => {
            $('#defaultId').val(us.id);
            $('#defaultFirstName').val(us.firstName);
            $('#defaultLastName').val(us.lastName);
            $('#defaultEmail').val(us.email);
            $('#defaultPassword').val(us.password);
        })

        fetch('api/v1/users/role-list').then(res => {
            res.json().then(roles => {
                $.each(roles, function (j, role) {
                    let roleOption = `<option value="${role.role}">${role.role.replaceAll('ROLE_', '')}</option>`
                    $('#defaultRoles').append(roleOption)
                })
            })
        });

    } else if(operation === 'delete') {
        document.getElementById('defaultModalSubmit').innerHTML = 'Delete'
        let roleJS = ''

        user.json().then(us => {
            defaultForm.find('input').prop('readonly', true);

            $('#defaultId').val(us.id);
            $('#defaultFirstName').val(us.firstName);
            $('#defaultLastName').val(us.lastName);
            $('#defaultEmail').val(us.email);
            $('#defaultPassword').val(us.password);
            $.each(us.roles, function (j, role) {
                roleJS += role.role.replaceAll('ROLE_', '') + ' '
            })
            $('#defaultRolesDeleteLab').attr({
                'class': 'my-0 show',
                'style': 'font-weight: bold'
            })
            $('#defaultRolesDelete').val(roleJS).attr({
                'class': 'form-control show',
                'style': 'height: 30px'
            })
            $('#defaultRolesLab').attr({
                'class': 'my-0 fade',
                'style': 'font-weight: bold; display: none'
            })
            $('#defaultRoles').attr({
                'class': 'form-control fade',
                'style': 'height: 55px; display: none'
            })
        })
    }
}

async function updateTable(operation, userId) {
    let usersTab = $('#usersTab tbody');

    if(operation === 'edit') {
        let user = await fetch(`api/v1/users/${userId}`).then(res => res.json());
        usersTab.find(`#${userId}`).remove()
        tableFilling(usersTab, user);
    } else if(operation === 'delete') {
        usersTab.find(`#${userId}`).remove()
    } else if(operation === 'add') {
        let user = await fetch(`api/v1/users/${userId}`).then(res => res.json());
        await tableFilling(usersTab, user);
        $('#users-tab').click();
    }
}

function tableFilling(table, user) {
    let roleJS = ''
    $.each(user.roles, function (j, role) {
        roleJS += role.role.replaceAll('ROLE_', '') + ' '
    })

    let tableFilling = `$(
                        <tr id="${user.id}">
                            <td>${user.id}</td>
                            <td>${user.firstName}</td>
                            <td>${user.lastName}</td>
                            <td>${user.email}</td>
                            <td>${roleJS}</td>
                            <td>
                                <button type="button" class="btn btn-primary" 
                                data-toggle="modal" onclick="clickUserButton(${user.id}, 'edit')">Edit</button>
                            </td>
                            <td>
                                <button type="button" class="btn btn-danger" 
                                data-toggle="modal" onclick="clickUserButton(${user.id}, 'delete')">Delete</button>
                            </td>
                        </tr>
                )`;
    table.append(tableFilling);
}

async function addNewUser() {
    let addTab = $('#addNewUser');

    defaultForm.attr('class', 'w-50').find('#modalFooter').attr({
        'class': 'modal-footer fade',
        'style': 'display: none'
    })

    addTab.append(defaultForm);

    defaultForm.one('submit', async (e) => {
        e.preventDefault();

        let body = {
            firstName: $('#defaultFirstName').val(),
            lastName: $('#defaultLastName').val(),
            email: $('#defaultEmail').val(),
            password: $('#defaultPassword').val(),
            roles: $('#defaultRoles').val()
        }

        let responseUserId;

        await fetch('/api/v1/users/update', {
            method: 'POST',
            headers: {'Content-type': 'application/json'},
            body: JSON.stringify(body)
        }).then(res => res.json()).then(us => {
            responseUserId = us.id;
        })

        console.log(responseUserId)

        await updateTable('add', responseUserId);
    })
}

// function updateTable() {
//     let formElement = document.getElementById('#defaultForm');
//
//     formElement.onsubmit = async (e) => {
//         await fetch('/api/v1/users/update', {
//             method: 'POST',
//             body: new FormData(formElement)
//         })
//     }
// }


// $('.table .editButton').on('click', function (event) {
//
//     event.preventDefault();
//
//     let href = $(this).attr('href');
//
//     $.get(href, function (user, status) {
//         $('#editId').val(user.id);
//         $('#editFirstName').val(user.firstName);
//         $('#editLastName').val(user.lastName);
//         $('#editEmail').val(user.email)
//         $('#editPassword').val(user.password);
//         $('#editRoles').val(user.roles);
//     });
//
//     $('#editModal').modal();
// });

// $('document').ready(function () {
//     $.ajax({
//         type: 'GET',
//         url: '/api/v1/users',
//         success: function (users) {
//
//             $.each(users, function (i, user) {
//                 $('#tabId').append(user.id)
//                 $('#tabFirstName').append(user.firstName)
//                 $('#tabLastName').append(user.lastName)
//                 $('#tabEmail').append(user.email)
//             })
//         }
//     });
// });