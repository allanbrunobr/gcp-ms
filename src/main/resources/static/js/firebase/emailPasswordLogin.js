const auth = firebase.auth();
const loginForm = document.getElementById('login-form');

if (loginForm) {
    loginForm.addEventListener('submit', function(event) {
        event.preventDefault(); // Evita o envio padrão do formulário
        const email = document.getElementById('email').value;
        const password = document.getElementById('password').value;

        auth.signInWithEmailAndPassword(email, password)
            .then((userCredential) => {
                const user = userCredential.user;
                // Login bem-sucedido, redirecione ou faça outra ação necessária
                if (user.emailVerified) {
                    // O endereço de email do usuário foi confirmado
                    console.log('Usuário logado com sucesso:', user);
                    window.location.href = "http://localhost:3001"; // Redireciona para a página principal
                } else {
                    const message = 'Endereço de email não confirmado ' + user.email;
                    // O endereço de email do usuário não foi confirmado
                    console.error('Endereço de email não confirmado:', user.email);
                    document.getElementById('message').textContent = message;

                    // Aqui você pode exibir uma mensagem de erro para o usuário
                }
            })
            .catch((error) => {
                // Ocorreu um erro durante o login
                const errorCode = error.code;
                const errorMessage = error.message;
                console.error('Erro ao fazer login:', errorMessage);
                // Aqui você pode exibir uma mensagem de erro para o usuário
                const errorElement = document.getElementById('error-message');
                errorElement.innerText = errorMessage;
            });
    });
}
