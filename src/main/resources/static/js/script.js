// Fecha actual
const hoy = new Date;
const anyo = hoy.getFullYear();
const mes = String(hoy.getMonth() + 1).padStart(2, '0');    
const dia = String(hoy.getDate()).padStart(2, '0');
const fechaMaxima = `${anyo}-${mes}-${dia}`;
// Formato del ID asterisco, ID padre + '-asterisk'
const asterisco = '-asterisk';


// Validaciones
document.addEventListener("DOMContentLoaded", () => {
    // Contruccion de los asteriscos
    let input = document.querySelectorAll('input[required]');
    Array.from(input).forEach(element => {
        let asterisk = document.createElement('span');
        asterisk.innerText = '*';
        asterisk.classList.add('requerido');
        asterisk.id = element.id + '-asterisk';
        if (element.id=='telefono') {
            element.parentNode.parentNode.insertBefore(asterisk, element.parentNode.nextSibling);
        }else{
            element.parentNode.insertBefore(asterisk, element.nextSibling);
        }
    });


    // Variables Input
    let inNombre = document.querySelector('#nombre');
    let inApellidos = document.querySelector('#apellidos');
    let inDNI = document.querySelector('#dni');
    let inEmail = document.querySelector('#email');
    let inPrefijo = document.querySelector('#prefijo');
    ponerPrefijos() // Se puede añadir mas mediante el array
    let inTelefono = document.querySelector('#telefono');
    let inCumpleanyos = document.querySelector('#anyo');
    let inCodPostal = document.querySelector('#codPostal');
    let checkPass, checkLong, checkMay, checkMin, checkEs // Variables para la guia
    let inPassword = document.querySelector('#password');
    let inConfPassword = document.querySelector('#confPassword');
    ponerBotonsMostrarContrasenya();
    let btnError = document.querySelector('#btnError');

    // Empieza el flujo
    if (inNombre.value == '') {
        inNombre.focus();
    }

    // Se declara la y unica alerta de la web
    let alerta = document.createElement('div');
    alerta.classList.add('alerta');


    // Eventos de Validacion
    // inNombre.addEventListener('blur', validarNombre);
    // inApellidos.addEventListener('blur', validarApellidos);
    // inDNI.addEventListener('blur', validarDNI);
    // inEmail.addEventListener('blur', validarEmail);
    // inPrefijo.addEventListener('input', cambiarFocoTelefono)
    // inTelefono.addEventListener('click', validarPrefijo)
    // inTelefono.addEventListener('blur', validarTelefono)
    // inCumpleanyos.addEventListener('input', cambiarColorFecha);
    // inCumpleanyos.addEventListener('blur', validarCumpleanyos);
    // maxFecha(inCumpleanyos);
    // inCodPostal.addEventListener('blur', validarCodPostal);
    inPassword.addEventListener('focus', ponerGuiaPassword);
    inPassword.addEventListener('input', guiaPassword);
    // inPassword.addEventListener('blur', validarPassword);
    // inConfPassword.addEventListener('blur', validarConfPassword);
    btnError.addEventListener('click', ocultarError);



    // Funciones de validacion Campo a Campo
    function validarCadena(cadena, asteriscoCampo) {
        if (cadena.length >= 5 && cadena.length <= 20) {
            if (cadena[0].toUpperCase() === cadena[0]) {
                return true;
            }
            getMensaje(asteriscoCampo, 'La primera letra tiene que ser Mayuscula.');
            return false;
        }
        getMensaje(asteriscoCampo, 'El campo tiene que tener una longitud de 5 a 20 caracteres.');
        return false;
    }
    
    // Validacion Nombre usa validarCadena
    function validarNombre() {
        let asteriscoNombre = getAsterico(inNombre);
        if (validarCadena(inNombre.value, inNombre)) {
            valido(asteriscoNombre);
            return true;
        }else{
            inValido(asteriscoNombre);
            inNombre.focus();
            return false;
        }
    }
    
    // Validacion Apellidos usa validarCadena
    function validarApellidos() {
        let asteriscoApellidos = getAsterico(inApellidos);
        if (validarCadena(inApellidos.value, inApellidos)) {
            valido(asteriscoApellidos);
            return true;
        }else{
            inValido(asteriscoApellidos);
            inApellidos.focus();
            return false;
        }   
    }
    
    // Validacion de DNI, realiza los calculos y da feedback persolaizado dependiendo del caso
    function validarDNI() {
        inDNI.value = inDNI.value.toUpperCase();
        let asteriscoDNI = getAsterico(inDNI);
        let dni = inDNI.value;
        let letra = dni.charAt(dni.length - 1);
        let numero = dni.substring(0, dni.length - 1);
        if (numero.length === 8 && letra.length === 1) {
            if (isNaN(numero)) {
                inDNI.focus();
                getMensaje(asteriscoDNI, 'El DNI tiene que ser un numero.');
                inValido(asteriscoDNI);
                return false;
            }
            if (letra === 'TRWAGMYFPDXBNJZSQVHLCKE'.charAt(numero % 23)) {
                valido(asteriscoDNI);
                return true;
            }
            inDNI.focus();
            getMensaje(asteriscoDNI, 'La letra no coincide con el numero.');
            inValido(asteriscoDNI);
            return false;
        }
        inDNI.focus();
        getMensaje(asteriscoDNI, 'El DNI tiene que tener 8 numeros y una letra.');
        inValido(asteriscoDNI);
        return false;
    }
    
    // Validacion de Email
    function validarEmail() {
        let asteriscoEmail = getAsterico(inEmail);
        let email = inEmail.value;
        const indiceArroba = email.indexOf('@');
        let user = email.substring(0, indiceArroba);
        let dominio = email.substring(indiceArroba+1, email.length);
        

        // Email
        // Solo un @
        const exArrobaExclusiva = /^[^@]*@[^@]*$/
        // Que no se repitan puntos seguidos
        const exRepeticionPuntos = /^(?!.*\.\.).*$/
        const exFinal = /(?<![.])$(?<![@])$/


        // Usuario y Dominio
        // Que no empiece por Numero
        const exPrincipio = /^(?![0-9])/
        // Que pueda contener
        const exContenidoUser = /[a-zA-Z0-9.]+$/

        // Dominio
        // Que no Empiece por '-' ni Termine, pero que pueda Contener
        const exGuionesPrincipioFinal = /^[^-].*[^-]$/
        // lo que puede contener
        const exContenidoDom = /^[a-zA-Z0-9.-]+$/
        // Que no se Repitan Consecutivamente
        const exRepeticionGuiones = /^(?!.*--).*$/

        if (user != '' || dominio != '') {
            if (exArrobaExclusiva.test(email)) {
                if (exPrincipio.test(user)) {
                    if (exContenidoUser.test(user)) {
                        if (exRepeticionPuntos.test(email)) {
                            if (exContenidoDom.test(dominio)) {
                                if (exGuionesPrincipioFinal.test(dominio) && exPrincipio.test(dominio)) {
                                    if (exRepeticionGuiones.test(dominio)) {
                                        if (exFinal.test(email)) {
                                            valido(asteriscoEmail);
                                            return true;
                                        }else{
                                            inEmail.focus();
                                            getMensaje(asteriscoEmail, 'No puede terminar por punto o @.');
                                            inValido(asteriscoEmail);
                                            return false;
                                        }
                                    }else{
                                        inEmail.focus();
                                        getMensaje(asteriscoEmail, 'No se pueden poner guiones consecutivos.');
                                        inValido(asteriscoEmail);
                                        return false;
                                    }
                                }else{
                                    inEmail.focus();
                                    getMensaje(asteriscoEmail, 'La parte del Dominio no puede empezar por numeros ni guiones, pampoco terminar por guiones.');
                                    inValido(asteriscoEmail);
                                    return false;
                                }
                            }else{
                                inEmail.focus();
                                getMensaje(asteriscoEmail, 'Caracteres No permitidos, solo se puede numeros, letras min o may, puntos y guiones.');
                                inValido(asteriscoEmail);
                                return false;
                            }
                        }else{
                            inEmail.focus();
                            getMensaje(asteriscoEmail, 'No se puede poner dos puntos seguidos.');
                            inValido(asteriscoEmail);
                            return false;
                        }
                    }else{
                        inEmail.focus();
                        getMensaje(asteriscoEmail, 'Caracteres No permitidos, solo se puede numeros, letras min o may, y puntos.');
                        inValido(asteriscoEmail);
                        return false;
                    }
                }else{
                    inEmail.focus();
                    getMensaje(asteriscoEmail, 'No puede empezar por Numero.');
                    inValido(asteriscoEmail);
                    return false;
                }
            }else{
                inEmail.focus();
                getMensaje(asteriscoEmail, 'Tiene que tener un @.');
                inValido(asteriscoEmail);
                return false;
            }
        }else{
            inEmail.focus();
            getMensaje(asteriscoEmail, 'No se puede dejar el campo Vacio.');
            inValido(asteriscoEmail);
            return false;
        }
    }

    // Funcion para dar estilo a la fecha como si tubiera atributo placeholder
    function cambiarColorFecha() {
        if (inCumpleanyos.value != '') {
            let color = getComputedStyle(document.documentElement).getPropertyValue('--texto').trim();
            cambiarColor(inCumpleanyos, color)
        }
    }

    // Funcion para impedir que se ponga una fecha superior a la actual en inCumpleanyos
    function maxFecha(input) {
        input.max = fechaMaxima;
    }
    
    // Validacion de Fecha, aun que no se pedia, se han teniendo encuenta dos casos ilogicos, como es vivir mas de 130 anyos, o nacer en el futuro
    function validarCumpleanyos() {
        let asteriscoFecha = getAsterico(inCumpleanyos);
        if (inCumpleanyos.value == '') {
            inCumpleanyos.focus();
            getMensaje(asteriscoFecha, 'No puedes dejar el campo vacio.');
            inValido(asteriscoFecha);
            return false;
        }
        if (inCumpleanyos.value <= fechaMaxima) {
            let diferencia = anyo-inCumpleanyos.value.substring(0, 4);
            console.log(diferencia);
            if (diferencia < 130 ) {
                valido(asteriscoFecha);
                return true;
            }else{
                inCumpleanyos.focus();
                getMensaje(asteriscoFecha, `No puedes tener ${diferencia} años.`);
                inValido(asteriscoFecha);
                return false;
            }
        }else{
            inCumpleanyos.focus();
            getMensaje(asteriscoFecha, 'No puedes nacer en el futuro.');
            inValido(asteriscoFecha);
            return false;
        }
    }

    // Validacion de Prefijo, se ha optado por usar un select, unificando el flujo, aun que se llama validacion es mas bien una funcion de dinamismo, dado que no compruba que los datos esten bien, aun que se podria con el array, el cual da los prefijos
    function validarPrefijo() {
        let asteriscoTelefono = getAsterico(inTelefono);
        if (inPrefijo.value!='+###') {
            cambiarFocoTelefono();
        }else{
            inPrefijo.focus();
            getMensaje(asteriscoTelefono, 'Seleccione el prefijo antes.')
            inValido(asteriscoTelefono);
        }
    }

    // Funcion para ayudar al flujo del formulario
    function cambiarFocoTelefono() {
        alerta.remove();
        moverBtnMostrar()
        inTelefono.focus();
    }

    // Validacion de Telefono, verifica la validez del mismo, em los doferentes casos, y siendo claro con las alertas
    function validarTelefono() {
        let asteriscoTelefono = getAsterico(inTelefono);
        if (inPrefijo.value!='+###') {
            let telefono = inTelefono.value;
            if (telefono.length === 9) {
                const primerNumero = /^(6|7)/
                if (primerNumero.test(telefono)) {
                    valido(asteriscoTelefono);
                    return true;
                }else{
                    inTelefono.focus();
                    getMensaje(asteriscoTelefono, 'El primer numeor tiene que ser 6 o 7.')
                    inValido(asteriscoTelefono);
                    return false;
                }
            }else{
                inTelefono.focus();
                getMensaje(asteriscoTelefono, 'La longitud tiene que ser de 9 numeros.')
                inValido(asteriscoTelefono);
                return false;
            }
        }else{
            inPrefijo.focus();
            getMensaje(asteriscoTelefono, 'Seleccione el prefijo antes.')
            inValido(asteriscoTelefono);
            return false;
        }
        
    }

    // Validacion de Codigo Postal, mediante dos expresiones regulares, la primera es un poco grande, pero se puede ver facil cual es su funcionamiento    
    function validarCodPostal() {
        let asteriscoFecha = getAsterico(inCodPostal);
        let codPostal = inCodPostal.value;
        if (codPostal.length == 5) {
            let provincia = codPostal.substring(0, 2)
            let distrito = codPostal.substring(2, 5)
            const rangoProviencia = /^(0[1-9]|[1-4][0-9]|50|51|52)$/;
            const rangoDistrito = /^\d{3}$/;
            if (rangoProviencia.test(provincia)&&rangoDistrito.test(distrito)) {
                valido(asteriscoFecha);
                return true;
            }else{
                inCodPostal.focus();
                getMensaje(asteriscoFecha, 'Código Postal invalido.');
                inValido(asteriscoFecha)
                return false;
            }
        }else{
            inCodPostal.focus();
            getMensaje(asteriscoFecha, 'La longitud tiene que ser de 5 numeros.');
            inValido(asteriscoFecha);
            return false;
        }
        
    }

    function ponerGuiaPassword() {
        if (document.querySelector('#guiaPassword')==undefined) {
            let asteriscoContrasenya = getAsterico(inPassword);
            let guiaPass = document.createElement('div');
            guiaPass.id = 'guiaPassword';
            guiaPass.classList.add('alerta');

            let parrafoLongitud = document.createElement('p');
            let parrafoMayuscula = document.createElement('p');
            let parrafoMinuscula = document.createElement('p');
            let parrafoEspecial = document.createElement('p');

            let requisitoLongitud = document.createElement('span');
            requisitoLongitud.id = 'checkLong';
            requisitoLongitud.classList.add('check');
            requisitoLongitud.setAttribute('cumplido', 'false');
            let requisitoMayuscula = document.createElement('span');
            requisitoMayuscula.id = 'checkMay';
            requisitoMayuscula.classList.add('check');
            requisitoMayuscula.setAttribute('cumplido', 'false');
            let requisitoMinuscula = document.createElement('span');
            requisitoMinuscula.id = 'checkMin';
            requisitoMinuscula.classList.add('check');
            requisitoMinuscula.setAttribute('cumplido', 'false');
            let requisitoEspecial = document.createElement('span');
            requisitoEspecial.id = 'checkEs';
            requisitoEspecial.classList.add('check');
            requisitoEspecial.setAttribute('cumplido', 'false');

            parrafoLongitud.appendChild(requisitoLongitud);
            parrafoLongitud.appendChild(document.createTextNode(' Longitud de 5-20 Caracteres.'));
            parrafoMayuscula.appendChild(requisitoMayuscula);
            parrafoMayuscula.appendChild(document.createTextNode(' Al menos una Mayuscula'));
            parrafoMinuscula.appendChild(requisitoMinuscula);
            parrafoMinuscula.appendChild(document.createTextNode(' Al menos una Minuscula'));
            parrafoEspecial.appendChild(requisitoEspecial);
            parrafoEspecial.appendChild(document.createTextNode(' Al menos un Caracter Especial'));

            guiaPass.appendChild(parrafoLongitud);
            guiaPass.appendChild(parrafoMayuscula);
            guiaPass.appendChild(parrafoMinuscula);
            guiaPass.appendChild(parrafoEspecial);
        
            asteriscoContrasenya.parentNode.insertBefore(guiaPass, asteriscoContrasenya.nextSibling);

            let requisitosX = document.querySelectorAll('span[cumplido="false"]');
            requisitosX.forEach(r => {
                r.innerText = 'X'
            });

            checkPass = document.querySelector('#guiaPassword');
            checkLong = document.querySelector('#checkLong');
            checkMay = document.querySelector('#checkMay');
            checkMin = document.querySelector('#checkMin');
            checkEs = document.querySelector('#checkEs');

            guiaPassword();
            moverBtnMostrar()
        }
    }

    // Funcion Contiene Letras, mediante expresion regular
    function contieneLetras(cadena, caracteres) {
        // Crear una expresión regular que coincida con cualquier carácter en la cadena 'caracteres'
        let regex = new RegExp(`[${caracteres}]`);
        return regex.test(cadena);
    }

    // Funcion Contiene Caracter Especial, mediante expresion regular
    function contieneCaracterEspecial(cadena) {
        // Cualquiera que no sea letra o número
        let regex = /[^a-zA-Z0-9]/;
        return regex.test(cadena);
    }
   
    // Guia de Contrasenya, indicando si la contrasenya esta bien
    function guiaPassword() {
        console.log(inPassword.value);
        let pass = inPassword.value;

        if (pass.length >= 5 && pass.length <= 20) {
            check(checkLong);
        }else{
            noCheck(checkLong)
        }

        let letras = 'qwertyuiopasdfghjklzxcvbnm';
        if (contieneLetras(pass, letras.toUpperCase())) {
            check(checkMay);
        }else{
            noCheck(checkMay)
        }
        
        if (contieneLetras(pass, letras)) {
            check(checkMin);

        }else{
            noCheck(checkMin)
        }
        
        if (contieneCaracterEspecial(pass)) {
            check(checkEs);
        }else{
            noCheck(checkEs)
        }
        moverBtnMostrar();
    }

    // Indica que el nodo de ayuda se Cumple
    function check(nodoCheck) {
        nodoCheck.setAttribute('cumplido', 'true');
        nodoCheck.innerText = '✔';
    }

    // Indica que el nodo de ayuda no se Cumple
    function noCheck(nodoCheck) {
        nodoCheck.setAttribute('cumplido', 'false');
        nodoCheck.innerText = 'X';
    }

    // Valida los requisitos de la Contrasenya, pero no indica nada al usuario
    function validarPassword() {
        let asteriscoContrasenya = getAsterico(inPassword);
        let pass = inPassword.value;
        let letras = 'qwertyuiopasdfghjklzxcvbnm';

        if (pass.length >= 5 && pass.length <= 20 && contieneLetras(pass, letras.toUpperCase()) && contieneLetras(pass, letras) && contieneCaracterEspecial(pass)) {
            checkPass.remove();
            inConfPassword.focus();
            valido(asteriscoContrasenya)
            return true;
        }else{
            inPassword.focus();
            inValido(asteriscoContrasenya);
            return false;
        }
    }
    

    // Validacion del segundo campo Contrasenya, solo los compara
    function validarConfPassword() {
        let asteriscoConfContrasenya = getAsterico(inConfPassword);
        if (inPassword.value == inConfPassword.value) {
            valido(asteriscoConfContrasenya);
            return true;
        }else{
            getMensaje(asteriscoConfContrasenya, 'Las contraseñas no Coinciden.');
            inValido(asteriscoConfContrasenya);
            return false;
        }
    }

    function cambiarColor(input, color) {
        input.style.color = color;
    }

    function getMensaje(nodoAsterisco, mensaje) {
        alerta.innerText = mensaje;
        nodoAsterisco.parentNode.insertBefore(alerta, nodoAsterisco.nextSibling);
        moverBtnMostrar()
    }
    
    function valido(asterisco) {
        asterisco.classList.remove('inValido');
        asterisco.classList.add('valido');
        alerta.remove();
        moverBtnMostrar()
    }

    function inValido(asterisco) {
        asterisco.classList.add('inValido');
        asterisco.classList.remove('valido');
    }
    
    function getAsterico(input) {
        return document.querySelector('#'+input.id+asterisco);
    }

    function ponerPrefijos() {
        const prefijosEuropeos = [
            "+34",  // España
            "+351", // Portugal
            "+33",  // Francia
            "+49",  // Alemania
            "+39",  // Italia
            "+44",  // Reino Unido
            "+31",  // Países Bajos
            "+32",  // Bélgica
            "+41",  // Suiza
            "+43",  // Austria
            "+46",  // Suecia
            "+47",  // Noruega
            "+45",  // Dinamarca
            "+358", // Finlandia
            "+30",  // Grecia
            "+48",  // Polonia
            "+7",   // Rusia
            "+353", // Irlanda
            "+420", // República Checa
            "+36"   // Hungría
        ];

        prefijosEuropeos.forEach(prefijo => {
            let opcion = document.createElement('option');
            opcion.innerText = prefijo;
            opcion.value = prefijo;
            inPrefijo.appendChild(opcion);
        });
    }

    // Crea los botones de ojo
    function crearBtnMostrar(nodo) {
        let ojo = document.createElement('span');
        ojo.id = getIdMostrar(nodo);
        ojo.innerText = '👁';
        ojo.setAttribute('mostrar', 'true');
        
        let asteriscoNodo = getAsterico(nodo);
        asteriscoNodo.parentNode.insertBefore(ojo, asteriscoNodo.nextSibling);
        ojo.onclick = function () {
            parpadeo(ojo, nodo);
        }
        ojo.style.left = (asteriscoNodo.offsetLeft - 45) +'px';
        ojo.style.top = (asteriscoNodo.offsetTop - 14) +'px';
    }

    function parpadeo(ojo, nodo) {
        console.log(ojo.getAttribute('mostrar'));
        
        if (ojo.getAttribute('mostrar')=='true') {
            ojo.setAttribute('mostrar', 'false')
            nodo.setAttribute('type', 'text')
            
        }else{
            ojo.setAttribute('mostrar', 'true')
            nodo.setAttribute('type', 'password')
        }
        moverBtnMostrar();
    }

    // Pone los botones a todos los inputs tipo password
    function ponerBotonsMostrarContrasenya() {
        let inputsPass = document.querySelectorAll('input[type="password"]');
        inputsPass.forEach(input => {
            if (getNodoMostrar(input) == undefined) {
                input.setAttribute('inputContrasenya', '');
                crearBtnMostrar(input);                
            }
        });
    }

    // Mueve los botones
    function moverBtnMostrar() {
        let inputsPass = document.querySelectorAll('input[inputContrasenya]');

        inputsPass.forEach(input => {
            let asteriscoInput = getAsterico(input);
            let ojo = getNodoMostrar(input)
            ojo.style.left = (asteriscoInput.offsetLeft - 45) +'px';
            ojo.style.top = (asteriscoInput.offsetTop - 14) +'px';
        });
    }

    // Genera la Id
    function getIdMostrar(nodo) {
        return 'btnMostrar-'+nodo.id;
    }

    // Consulta el nodo
    function getNodoMostrar(nodo) {
        return document.querySelector('#'+getIdMostrar(nodo));
    }

    window.addEventListener('resize', function(event) {
        moverBtnMostrar();
    });


    function ocultarError() {
        document.querySelector('#formError').classList.add('oculto');
    }


})

