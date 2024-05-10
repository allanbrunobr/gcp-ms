const auth = firebase.auth();
const googleProvider = new firebase.auth.GoogleAuthProvider();
const googleLoginBtn = document.getElementById('login-google');

if (googleLoginBtn) {
    googleLoginBtn.addEventListener('click', async function () {
        await auth.signInWithPopup(googleProvider)
            .then(function (result) {
                const credential = firebase.auth.GoogleAuthProvider.credentialFromResult(result);
                const user = result.user;
                console.log(user);
                window.location.href = "/indexGCP";

            }).catch((error) => {
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


const logoutLink = document.getElementById('logout-link');
if (logoutLink) {
    logoutLink.addEventListener('click', function () {
        auth.signOut()
            .then(() => {
                // Revoga o token de acesso do Google
                return auth().currentUser.reauthenticateWithPopup(new firebase.auth.GoogleAuthProvider());
            }).then(() => {
                console.log('Usuário deslogado com sucesso');
                window.location.href = "/logout-gcp"; // redireciona para a página de login
            })
            .catch((error) => {
                console.log('Erro ao deslogar usuário: ', error);
            });
    });

}


