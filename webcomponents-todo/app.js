var keycloak = new Keycloak({
    "url": "https://keycloak.jarry.dk:8543/",
    "realm": "playground",
    "clientId": "todo-playground-service"
});

var restServiceUrl = 'https://todo.jarry.dk:8443/todos';

var requestedScopes = "openid email profile phone";

var jarryTodos = document.querySelector("jarry-todos");

function notAuthenticated() {
    jarryTodos.dispatchEvent(new Event("keycloak-not-authenticated"));
    document.getElementById('not-authenticated').style.display = 'block';
    document.getElementById('authenticated').style.display = 'none';
}
keycloak.onAuthLogout = notAuthenticated;

function authenticated() {
    jarryTodos.dispatchEvent(new Event("keycloak-authenticated"));
    document.getElementById('not-authenticated').style.display = 'none';
    document.getElementById('authenticated').style.display = 'block';
    document.getElementById('user').value = keycloak.tokenParsed['preferred_username'];
    updateTokensDisplay();
}

function updateTokensDisplay(){
    if (keycloak.authenticated) {
        let idTokenObject = prettyStringJwt(keycloak.idToken);
        let tokenObject = prettyStringJwt(keycloak.token);

        document.getElementById('idToken').innerHTML =  '<pre class="preJsonTxt">' +idTokenObject + '</pre>';
        document.getElementById('idTokenBase64').innerHTML = '<code>' + keycloak.idToken + '</code>';

        document.getElementById('tokenBase64').innerHTML = '<code>' + keycloak.token + '</code>';
        document.getElementById('token').innerHTML = '<pre class="preJsonTxt">' + tokenObject + '</pre>';
    }
}

function initKeycloak(){

    console.debug("requestedScopes", requestedScopes);

    keycloak.init({
        onLoad: 'check-sso',
        checkLoginIframeInterval: 1,
        scope : requestedScopes
    }).then(function () {
        if (keycloak.authenticated) {
            authenticated();
        } else {
            notAuthenticated();
        }
        document.body.style.display = 'block';
    });
}

window.onload = function () {

    var cookieScope = getCookie('scope');
    if (cookieScope != ''){
        requestedScopes = cookieScope;
    }
    document.getElementById('inputScope').value = requestedScopes;

    /**
     * Update tokens after scope have been updated
     */
    const scopeForm = document.getElementById("scopeForm");
    scopeForm.addEventListener("submit", (event) => {
        event.preventDefault();
        console.log("Scope updated....");
        requestedScopes = document.getElementById('inputScope').value;
        setCookie('scope', requestedScopes, 10);
        initKeycloak();
        keycloak.login();
    });

    initKeycloak();
}

function prettyStringJwt(token){
    return JSON.stringify(parseJwt(token), null, 4);
}

function parseJwt(token) {
    let base64Url = token.split('.')[1];
    let base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    let jsonPayload = decodeURIComponent(window.atob(base64).split('').map(function(c) {
        return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
    }).join(''));
    return JSON.parse(jsonPayload);
}

function setCookie(cname, cvalue, exdays) {
    const d = new Date();
    d.setTime(d.getTime() + (exdays*24*60*60*1000));
    let expires = "expires="+ d.toUTCString();
    document.cookie = cname + "=" + cvalue + ";" + expires + ";path=/";
}

function getCookie(cname) {
    let name = cname + "=";
    let decodedCookie = decodeURIComponent(document.cookie);
    let ca = decodedCookie.split(';');
    for(let i = 0; i <ca.length; i++) {
      let c = ca[i];
      while (c.charAt(0) == ' ') {
        c = c.substring(1);
      }
      if (c.indexOf(name) == 0) {
        return c.substring(name.length, c.length);
      }
    }
    return "";
}

function request(endpoint) {
    let req = function() {
        let req = new XMLHttpRequest();
        let payload = document.getElementById('payload');
        req.open('GET', restServiceUrl + endpoint , true);

        if (keycloak.authenticated) {
            req.setRequestHeader('Authorization', 'Bearer ' + keycloak.token);
        }

        req.onreadystatechange = function () {
            if (req.readyState == 4) {
                if (req.status == 200) {
                    let payloadObject = JSON.stringify(JSON.parse(req.responseText+""), null, 4);
                    payload.innerHTML = '<pre class="preJsonTxt">' + payloadObject + '</pre>';
                } else if (req.status == 0) {
                    payload.innerHTML = '<span class="error">Request failed</span>';
                } else {
                    payload.innerHTML = '<span class="error">' + req.status + ' ' + req.statusText + '</span>';
                }
            }
        };

        req.send();
    };

    if (keycloak.authenticated) {
        keycloak.updateToken(30)
            .then(req);
    } else {
        req();
    }
}