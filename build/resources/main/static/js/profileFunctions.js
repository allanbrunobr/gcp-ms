$(document).ready(function() {
    $('#password, #confirmPassword').on('keyup', function () {
        if ($('#password').val() == $('#confirmPassword').val()) {
            $('#confirmPassword').removeClass('is-invalid').addClass('is-valid');
        } else {
            $('#confirmPassword').removeClass('is-valid').addClass('is-invalid');
        }
    });
});

// Password and confirmPassword fields validation
let passwordField = document.getElementById('password');
let confirmPasswordField = document.getElementById('confirmPassword');
let passwordFeedback = document.getElementById('password-feedback');
let confirmPasswordFeedback = document.getElementById('confirmPassword-feedback');

passwordField.addEventListener('focusout', function() {
    const password = passwordField.value;
    const isValid = /^(?=.*\d)(?=.*[a-zA-Z])(?=.*\W)(?=.*\S).{8,20}$/.test(password);
    if (isValid) {
        passwordField.classList.add('is-valid');
        passwordFeedback.classList.remove('invalid-feedback');
        passwordFeedback.classList.add('valid-feedback');
        passwordFeedback.textContent = 'Senha válida.';
        confirmPasswordField.removeAttribute('readonly');
    } else {
        passwordField.classList.remove('is-valid');
        passwordFeedback.classList.remove('valid-feedback');
        passwordFeedback.classList.add('invalid-feedback');
        passwordFeedback.textContent = 'A senha deve ter entre 8 e 20 caracteres e conter pelo menos um número, uma letra, um caractere especial e não pode conter espaços em branco.';
        confirmPasswordField.setAttribute('readonly', 'readonly');
        confirmPasswordField.value = '';
        confirmPasswordField.classList.remove('is-valid');
        confirmPasswordFeedback.classList.remove('valid-feedback');
        confirmPasswordFeedback.classList.add('invalid-feedback');
    }
});

confirmPasswordField.addEventListener('input', function() {
    const confirmPassword = confirmPasswordField.value;
    const password = passwordField.value;
    if (confirmPassword === password) {
        confirmPasswordField.classList.add('is-valid');
        confirmPasswordFeedback.classList.remove('invalid-feedback');
        confirmPasswordFeedback.classList.add('valid-feedback');
        confirmPasswordFeedback.textContent = 'Senhas coincidem.';
    } else {
        confirmPasswordField.classList.remove('is-valid');
        confirmPasswordFeedback.classList.remove('valid-feedback');
        confirmPasswordFeedback.classList.add('invalid-feedback');
        confirmPasswordFeedback.textContent = 'As senhas não coincidem.';
    }
});

(function() {
    'use strict';
    window.addEventListener('load', function() {
        const forms = document.getElementsByClassName('needs-validation');
        Array.prototype.filter.call(forms, function(form) {
            form.addEventListener('submit', function(event) {
                if (form.checkValidity() === false) {
                    event.preventDefault();
                    event.stopPropagation();
                }
                form.classList.add('was-validated');
            }, false);
        });
    });
})();

$().ready(function() {
    $("form").submit(function (event) {
        const form = $(this)[0];
        if (form.checkValidity() === false) {
            event.preventDefault();
            event.stopPropagation();
            form.classList.add('was-validated');
        }
        else {
            event.preventDefault();
            const formData = {
                primeiroNome: $("#primeiroNome").val(),
                segundoNome: $("#segundoNome").val(),
                email: $("#email").val(),
                password: $("#password").val(),
                cep: $("#cep").val()
            };

            $.ajax({
                type: 'POST',
                contentType: 'application/json;charset=utf-8',
                dataType: 'json',
                url: '/create',
                data: JSON.stringify(formData),
                success: function (response) {
                    console.log(response);
                    // Handle response here
                },
                error: function (error) {
                    console.error(error);
                    // Handle errors here
                }
            });
        }
    });
});