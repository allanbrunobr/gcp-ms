const auth = firebase.auth();
const gitHubProvider = new firebase.auth.GithubAuthProvider();
const gitHubLoginBtn = document.getElementById('login-github');

if (gitHubLoginBtn) {
    gitHubLoginBtn.addEventListener('click', async function () {
        await auth.signInWithPopup(gitHubProvider)
            .then(function (result) {
                const credential = firebase.auth.GithubAuthProvider.credentialFromResult(result);
                const user = result.user;
                console.log(user);
                window.location.href = `/indexGCP?name=${encodeURIComponent(user.displayName)}&photo=${encodeURIComponent(user.photoURL)}`;
            }).catch((error) => {
                const errorCode = error.code;
                const errorMessage = error.message;
            });
    });
}


const logoutLink = document.getElementById('logout-link');
if (logoutLink) {
    logoutLink.addEventListener('click', function () {
        auth.signOut()
            .then(() => {
                console.log('Usuário deslogado com sucesso');
                window.location.href = "/logout-gcp"; // redireciona para a página de login
            })
            .catch((error) => {
                console.log('Erro ao deslogar usuário: ', error);
            });
    });

}
