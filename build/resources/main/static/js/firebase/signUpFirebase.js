const signupForm = document.getElementById('signup-form');

if (signupForm) {
    signupForm.addEventListener('submit', function(event) {
        event.preventDefault(); // Evita o envio padrão do formulário

        // Obtenha os valores dos campos do formulário de registro
        const email = document.querySelector('input[name="email"]').value;
        const password = document.querySelector('input[name="password"]').value;

        // Crie o usuário com email e senha
        firebase.auth().createUserWithEmailAndPassword(email, password)
            .then((userCredential) => {
                const user = userCredential.user;
                // Envia o email de verificação
                user.sendEmailVerification()
                    .then(() => {
                        // Email de verificação enviado com sucesso
                        console.log('Email de verificação enviado para:', user.email);
                        const message = 'Email de verificação enviado para: ' + user.email;
                        window.location.href = "/?emailVerification=true&message=" + encodeURIComponent(message);
                    })
                    .catch((error) => {
                        // Ocorreu um erro ao enviar o email de verificação
                        console.error('Erro ao enviar o email de verificação:', error);
                        // Redireciona para a página de login sem exibir a mensagem de verificação
                        window.location.href = "/";
                    });
            })
            .catch((error) => {
                // Ocorreu um erro durante o registro
                const errorCode = error.code;
                const errorMessage = error.message;
                console.error('Erro ao registrar usuário:', errorMessage);
                if (errorCode === 'auth/email-already-in-use') {
                    document.getElementById('error-message').textContent = 'Este endereço de email já está em uso. Por favor, escolha outro endereço de email.';
                } else if (errorCode === 'auth/invalid-email') {
                    document.getElementById('error-message').textContent = 'Endereço de email inválido. Por favor, insira um endereço de email válido.';
                } else if (errorCode === 'auth/weak-password') {
                    document.getElementById('error-message').textContent = 'Senha fraca. Por favor, escolha uma senha mais forte.';
                } else {
                    document.getElementById('error-message').textContent = 'Ocorreu um erro durante o registro. Por favor, tente novamente mais tarde.';
                }
            });
    });
}

const backButton = document.getElementById('back-button');

if (backButton) {
    backButton.addEventListener('click', function() {
        // Redireciona o usuário para a página anterior
        window.history.back();
    });
}