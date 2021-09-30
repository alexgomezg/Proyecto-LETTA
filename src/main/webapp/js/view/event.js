var EventView = (function () {
    var dao;

    // Referencia a this que permite acceder a las funciones pÃºblicas desde las funciones de jQuery.
    var self;
    var actualPage = -9;
    var numEvents = 0;
    var arrayEvents = [];
    var contEvents = 0
    var user_token = null;

    var txtToFilt = "none";

    function EventView(eventDao, userDao) {
        usersDAO = userDao;
        dao = eventDao;
        self = this;
        this.init = function (page) {
            contEvents = 0;
            user_token = localStorage.getItem('user');
            insertSearchDivContent();
            //Si esta autorizado muestra el usuario si no muestra el boton de inciar sesión
            if (user_token === null) {
                insertLoginContentUnauthorized();
            } else {
                insertLoginContentAuthorized();
            }
            insertMainHtmlContent(); //Inserta la estructura básica de html de la web
            //El número -1 en constructor de init se utiliza para resetar la vista a los valores de inicio
            if (page === -1) {
                actualPage = 0;
            } else {
                actualPage += page;
            }

            if (txtToFilt === 'none') { //Si no hay texto a filtrar se listan los normales
                getListImportantEvents(); //Se renderizan los enventos importantes
                getNumNormalEvents();
                getListNormalEvents();
            } else { //Si no hay texto a filtrar se cuenta el numero de eventos que cumplen las condiciones         
                getNumFilterNormalEvents();
                if (numEvents !== 0) { //Si es diferente de 0 se muestran los eventos filtrados
                    getListFilterNormalEvents();
                    document.getElementById('tittleEvents').innerHTML = "Resultados de la búsqueda: " + txtToFilt;
                    document.getElementById('listEvents').className = "container-lg";

                } else { //Si no hay se muestra un mensaje de error y se muestran todos sin filtrar
                    document.getElementById("modalText").className = "alert alert-danger"
                    document.getElementById('modalText').innerHTML = "No hay eventos que coincidan con tu búsqueda";
                    document.getElementById('showModal').click();
                    txtToFilt = "none";
                    getListImportantEvents();
                    getNumNormalEvents();
                    getListNormalEvents();
                }
            }

            //Se añaden los botones de paginar y todos los listener  
            document.getElementById("listEvents").innerHTML += '<div class="col-md-12 mb-5 text-center" id="plusMinusButtons"></div>';
            insertPaginationButtons();
            for (var i = 0; i < arrayEvents.length; i++) {
                addViewButtonListener(arrayEvents[i]);
            }
            addSearchListener();
            addButtonsListeners();
            addLogInListeners();
            addCreateEventListener();
        };
    }
    ;

    //////////////////HTML CONTENT SETTERS///////////////////////////////////////////////////////////////

    var insertMainHtmlContent = function () {
        var txt = '<div id="myCarousel" class="carousel slide" data-bs-ride="carousel">\
    <div class="carousel-indicators" id="indicators"></div>\
    <div class="carousel-inner"><div id="slider"></div></div>\
      <button class="carousel-control-prev" type="button" data-bs-target="#myCarousel" data-bs-slide="prev">\
        <span class="carousel-control-prev-icon" aria-hidden="true"></span>\
        <span class="visually-hidden">Previous</span>\
      </button>\
      <button class="carousel-control-next" type="button" data-bs-target="#myCarousel" data-bs-slide="next">\
        <span class="carousel-control-next-icon" aria-hidden="true"></span>\
        <span class="visually-hidden">Next</span>\
      </button>\
    </div>\
    <div class="container-lg" id="arriba">\
    <div class="row px-3 py-3 mt-5 mx-auto">\
    <div class="col-md-8 d-flex justify-content-end"><h1 id="tittleEvents">Todos nuestros eventos</h1></div>';

        if (user_token != null) {
            txt += '<div class="col-6 col-md-4 d-flex justify-content-end"><button type="button" class="btn btn-sm btn-outline-secondary btn-block align-self-center" id="createEvent">Crear evento</button></div>';
        }

        txt += '</div><div class="row" id="listEvents"></div></div>';
        document.getElementById("mainId").innerHTML = txt;

    }

    var insertLoginContentUnauthorized = function () {
        document.getElementById("divInicio").innerHTML += '<form class="d-flex mt-2" id="divInicio"></form>\
      <ul class="navbar-nav me-auto my-2 my-lg-0">\
        <li class="nav-item" style="width: max-content;">\
          <a class="btn btn-outline-secondary ms-2" id="signUp"><small>Iniciar Sesión</small></a>\
        </li>\
      </ul>';
    }

    var insertLoginContentAuthorized = function () {
        document.getElementById("divInicio").innerHTML += '<form class="d-flex mt-2" id="divInicio"></form>\
  <ul class="navbar-nav me-auto my-2 my-lg-0">\
    <li class="nav-item dropdown">\
      <a class="nav-link dropdown-toggle" id="navbarScrollingDropdown" role="button"\
        data-bs-toggle="dropdown" aria-expanded="false"><i class="bi bi-person-fill me-1 ms-2"></i>' + user_token + '</a>\
      <a class="dropdown-menu btn btn-sm btn-secondary text-center mt-2" aria-labelledby="navbarScrollingDropdown" id="logOut"><small>Cerrar Sesion</small></a>\
    </li>\
  </ul>';
    }

    var insertLoginFormContent = function () {
        document.getElementById('modalDialog').className = "modal-dialog modal-lg modal-fullscreen-sm-down ";
        document.getElementById('modalText').innerHTML = '<div class=" d-flex justify-content-center">\
    <form id="form-singin">\
        <h2 class="mb-3 text-center">Iniciar Sesión</h2>\
        <div class="input-group input-group-lg mb-3 ">\
                <span class="input-group-text border-0" id="userLogIn"><i class="bi bi-person-fill"></i></span>\
            <div class="form-floating">\
                <input type="text" class="form-control" id="login" placeholder="Usuario" aria-describedby="userLogIn" size= "40" required>\
                <label for="login">Usuario</label>\
            </div>\
        </div>\
        <div class="input-group input-group-lg mb-3">\
                <span class="input-group-text border-0" id="userPass"><i class="bi bi-lock-fill"></i></span>\
            <div class="form-floating">\
                <input type="password" class="form-control" id="password" placeholder="Contraseña" size= "40" required>\
                <label for="password">Contraseña</label>\
            </div>\
        </div>\
        <div class=" mt-3">\
        <button type="button" class="btn btn-lg btn-secondary btn-block mt-3 btn-block" id="enterButton" style="width: -webkit-fill-available;">Entrar</button>\
    </form>\
    </div>';
    }

    var firstSliderItem = function (event) {
        var description = event.description.substring(0, 710) + ' ...';
        var type = event.image.charAt(0);
        var date = new Date(event.day);
        var fec = String(date);
        var formatedDate = date.toLocaleDateString() + "(" + fec.substring(fec.indexOf("GMT") - 9, fec.indexOf("GMT") - 4) + ")";
        var slider = '<div class="carousel-item active" style="background:url(data:image/' + type + ';base64,' + event.image + ') center; background-size: cover;">\
    <div class="container">\
      <div class="carousel-caption text-end">\
        <h1>' + event.name + '</h1>\
        <div class="text-end" style="width: 50%;float: right;">\
        <p>' + description + '</p>\
        <p><i class="bi bi-geo-alt-fill" style="padding-left: 1rem; padding-right: 0.5rem;"></i>' + event.location + '<i class="bi bi-calendar-date" style="padding-left: 1rem; padding-right: 0.5rem;"></i>' + formatedDate +
                '<i class="bi bi-tag" style="padding-left: 1rem; padding-right: 0.5rem;"></i>' + event.tag + '<i class="bi bi-people" style="padding-left: 1rem; padding-right: 0.5rem;"></i>' + devolverNumAsistentesEvento(event.id) + '/' + event.capacity + '</p>\
        <p><a class="btn btn-lg btn-outline-light" id="event' + event.id + '" href="#" role="button">Ver</a></p>\
        </div>\
      </div>\
    </div>\
  </div>'
        document.getElementById("slider").innerHTML += slider;
    }

    var sliderItem = function (event) {
        var description = event.description.substring(0, 810) + ' ...';
        var type = event.image.charAt(0);
        var date = new Date(event.day);
        var fec = String(date);
        var formatedDate = date.toLocaleDateString() + "(" + fec.substring(fec.indexOf("GMT") - 9, fec.indexOf("GMT") - 4) + ")";
        var slider = '<div class="carousel-item" style="background:url(data:image/' + type + ';base64,' + event.image + ') center; background-size: cover;">\
    <div class="container">\
      <div class="carousel-caption text-end">\
        <h1>' + event.name + '</h1>\
        <div class="text-end" style="width: 50%;float: right;">\
        <p>' + description + '</p>\
        <p><i class="bi bi-geo-alt-fill" style="padding-left: 1rem; padding-right: 0.5rem;"></i>' + event.location + '<i class="bi bi-calendar-date" style="padding-left: 1rem; padding-right: 0.5rem;"></i>' + formatedDate +
                '<i class="bi bi-tag" style="padding-left: 1rem; padding-right: 0.5rem;"></i>' + event.tag + '<i class="bi bi-people" style="padding-left: 1rem; padding-right: 0.5rem;"></i>' + devolverNumAsistentesEvento(event.id) + '/' + event.capacity + '</p>\
        <p><a class="btn btn-lg btn-outline-light" id="event' + event.id + '" href="#" role="button">Ver</a></p>\
        </div>\
      </div>\
    </div>\
  </div>'
        document.getElementById("slider").innerHTML += slider;
    }

    var addCardEvent = function (event) {
        var type = event.image.charAt(0);
        var description = event.description.substring(0, 180) + ' ...';
        var date = new Date(event.day);
        var fec = String(date);
        var formatedDate = date.toLocaleDateString() + "(" + fec.substring(fec.indexOf("GMT") - 9, fec.indexOf("GMT") - 4) + ")";
        var card = '<div class="col-lg-4 mb-3">\
      <div class="card mb-4 h-100 overflow-hidden text-white bg-dark rounded-5 shadow-lg hover hover-3 text-white rounded" style="background:url(data:image/' + type + ';base64,' + event.image + ') center ; background-size: cover;">\
      <div class="hover-overlay"></div>\
          <div class="hover-3-content d-flex flex-column p-5 pb-3 text-white text-shadow-1">\
            ' + cardNameSize(event) + '<div class="card-icons"><ul class="d-flex list-unstyled mt-auto">\
            <li class="d-flex align-items-center">\
              <i class="bi bi-geo-alt-fill" style="padding-left: 1rem; padding-right: 0.5rem;"></i>\
              ' + event.location + '\
            </li>\
            <li class="d-flex align-items-center">\
              <i class="bi bi-calendar-date" style="padding-left: 1rem; padding-right: 0.5rem;"></i>\
              ' + formatedDate + '\
            </li>\
          </ul>\
          <ul class="d-flex list-unstyled mt-auto">\
              <li class="d-flex align-items-center">\
                <i class="bi bi-tag" style="padding-left: 1rem; padding-right: 0.5rem;"></i>\
                ' + event.tag + '\
              </li>\
              <li class="d-flex align-items-center">\
                <i class="bi bi-people" style="padding-left: 1rem; padding-right: 0.5rem;"></i>\
                ' + devolverNumAsistentesEvento(event.id) + '/' + event.capacity + '\
              </li>\
            </ul>\
          </div>\
          <div class=" hover-3-description pt-4 text-start mb-auto"><p>' + description + ' </p>\
            <ul class="d-flex list-unstyled mt-auto">\
              <li class="d-flex align-items-center">\
                <i class="bi bi-geo-alt-fill" style="padding-left: 1rem; padding-right: 0.5rem;"></i>\
                ' + event.location + '\
              </li>\
              <li class="d-flex align-items-center">\
                <i class="bi bi-calendar-date" style="padding-left: 1rem; padding-right: 0.5rem;"></i>\
                ' + formatedDate + '\
              </li>\
            </ul>\
            <ul class="d-flex list-unstyled mt-auto">\
              <li class="d-flex align-items-center">\
                <i class="bi bi-tag" style="padding-left: 1rem; padding-right: 0.5rem;"></i>\
                ' + event.tag + '\
              </li>\
              <li class="d-flex align-items-center">\
                <i class="bi bi-people" style="padding-left: 1rem; padding-right: 0.5rem;"></i>\
                ' + devolverNumAsistentesEvento(event.id) + '/' + event.capacity + '\
              </li>\
            </ul>\
          </div>\
        </div>\
        <button type="button" class="btn btn-lg btn-outline-light btn-block btn-card" id="event' + event.id + '">Ver</button>\
      </div>\
    </div>'
        document.getElementById("listEvents").innerHTML += card;
    }

    var insertPaginationButtons = function () {
        if (actualPage >= 9) {
            document.getElementById("plusMinusButtons").innerHTML += '<button type="button" class="btn btn-outline-secondary me-1" id="minusPage"><i class="bi bi-caret-left"></i>Anterior</button>';
        }

        if (actualPage + 9 < numEvents) {
            document.getElementById("plusMinusButtons").innerHTML += '<button type="button" class="btn btn-outline-secondary ms-1" id="plusPage">Siguiente<i class="bi bi-caret-right"></i></button>';
        }
    }

    var cardNameSize = function (event) {
        var nombre = ' '
        if (event.name.length > 35) {
            nombre = '<h4 class="pt-5 mt-5 mb-4 lh-1 fw-bold hover-3-title">' + event.name + '</h4>';
        } else {
            if (event.name.length > 21) {
                nombre = '<h3 class="pt-5 mt-5 mb-4 lh-1 fw-bold hover-3-title">' + event.name + '</h3>';
            } else {
                if (event.name.length > 17) {
                    nombre = '<h2 class="pt-5 mt-5 mb-4 lh-1 fw-bold hover-3-title">' + event.name + '</h2>';
                } else {
                    nombre = '<h1 class="pt-5 mt-5 mb-4 lh-1 fw-bold hover-3-title">' + event.name + '</h1>';
                }
            }
        }
        return nombre;
    }

    var showEvent = function (event) {
        var date = new Date(event.day);
        var fec = String(date);
        var formatedDate = date.toLocaleDateString() + "(" + fec.substring(fec.indexOf("GMT") - 9, fec.indexOf("GMT") - 4) + ")";
        document.getElementById("mainId").innerHTML = '';
        var user_id = localStorage.getItem('user_id');
        var txt = '<div class="blog-slider border rounded-5 rounded">'

        if (user_id == event.organizer) {
            txt += '<button class="position-absolute top-100 start-50 translate-middle badge btn p-2 btn-secondary" id="deleteEventButton"><i class="bi bi-file-earmark-x me-1"></i>Eliminar<span class="visually-hidden">delete event</span></button>';
         // txt+='<button class="position-absolute top-100 start-50 translate-middle badge btn p-2" id="deleteEventButton"><i class="bi bi-file-earmark-x me-1" style="margin-right: 1rem;"></i>Eliminar</button></input>';
        }

        txt += '<div class="blog-slider__wrp">\
      <div class="blog-slider__item">\
        <div class="blog-slider__img img-wrapper-20" >\
          <img src="data:image/png;base64, ' + event.image + '" class="img-fluid img-thumbnail shadow-none rounded-5 rounded" alt="Sheep 11"></img>\
        </div>\
        <div class="blog-slider__content">\
          <span class="blog-slider__code_tag"><ul class="d-flex list-unstyled mt-auto">\
            <li class="d-flex align-items-center">\
              <i class="bi bi-tag" style="padding-left: 1rem; padding-right: 0.5rem;"></i>\
              <small>' + event.tag + '</small>\
            </li>\
          </ul>\</span>\
          <div class="blog-slider__title">' + event.name + '</div>\
          <span class="blog-slider__code"><ul class="d-flex list-unstyled mt-auto">\
          <li class="d-flex align-items-center">\
            <i class="bi bi-geo-alt-fill" style="padding-right: 0.5rem;"></i>\
            <small>' + event.location + '</small>\
          </li>\
          <li class="d-flex align-items-center">\
            <i class="bi bi-calendar-date" style="padding-left: 1rem; padding-right: 0.5rem;"></i>\
            <small>' + formatedDate + '</small>\
          </li>\
            <li class="d-flex align-items-center">\
              <i class="bi bi-people" style="padding-left: 1rem; padding-right: 0.5rem;"></i>\
              <small id="etiquetaCapacidad">' + devolverNumAsistentesEvento(event.id) + '/' + event.capacity + '</small>\
            </li>\
          </ul>\</span>\
          <div class="blog-slider__text">' + event.description + '</div>\
          <div class="justify-content-center mt-5" align="center" id="joinButtonContainer">\
          <input type="hidden" id="idEvento" value="' + event.id + '">';

        if (user_token != null) {
            var user_id = localStorage.getItem('user_id');
            dao.isAssistantOfEvent(event.id, user_id, function (aux) {
                if (aux == 0) {
                    if (devolverNumAsistentesEvento(event.id) < event.capacity) {
                        txt += '<button id="joinButton" type="button" class="btn btn-secondary me-1"><i class="bi bi-calendar-check-fill" style="margin-right: 1rem;"></i>Inscribirse</button></input>';
                    }
                } else {
                    txt += '<button id="unJoinButton" type="button" class="btn btn-secondary me-1"><i class="bi bi-calendar-x-fill" style="margin-right: 1rem;"></i>Cancelar asistencia</button></input>';
                }
            },
                    function () {
                    document.getElementById("modalText").className = "alert alert-danger"
                    document.getElementById('modalText').innerHTML = "No ha sido posible acceder al listado de asistentes";
                    document.getElementById('showModal').click();
                    });
        }

        txt += '</div></div></div></div>'
        document.getElementById("mainId").innerHTML = txt;

        var back = '<div class="justify-content-center mt-5 mb-5" align="center">\
    <button type="button" class="btn btn-outline-secondary me-1" id="back"><i class="bi bi-caret-left"></i>Volver</button></div>'
        document.getElementById("mainId").innerHTML += back;
        addBackListener();
        addListenerUserEventRegister();
        addListenerCancelUserEventRegister();
        deleteEventListener();
    }

    var insertSearchDivContent = function () {
        var txt = '<div class="input-group mb-1 mt-1">\
      <input type="search" class="form-control" placeholder="Buscar" id="searchText" aria-label="search" aria-describedby="button-addon2">\
      <button class="btn btn-secondary" type="button" id="searchButton"><i class="bi bi-search"></i></button>\
      </div>';
        document.getElementById("divInicio").innerHTML = txt;

    }


    var showListEventsSearch = function (event) {
        var date = new Date(event.day);
        var fec = String(date);
        var formatedDate = date.toLocaleDateString() + "(" + fec.substring(fec.indexOf("GMT") - 9, fec.indexOf("GMT") - 4) + ")";
        var description = event.description.substring(0, 185) + ' ...';
        var txt = '<div class="blog-slider-search border rounded-5 rounded">\
                <div class="blog-slider__wrp">\
                  <div class="blog-slider__item">\
                    <div class="blog-slider__img_search img-wrapper-20">\
                        <img src="data:image/png;base64, ' + event.image + '" class="img-fluid img-thumbnail shadow-none rounded-5 rounded" alt="Sheep 11">\
                    </div>\
                    <div class="blog-slider__content">\
                      <span class="blog-slider__code_tag">\
                          <ul class="d-flex list-unstyled mt-auto">\
                              <li class="d-flex align-items-center">\
                                  <i class="bi bi-tag" style="padding-left: 1rem; padding-right: 0.5rem;"></i>\
                                  <small>' + event.tag + '</small>\
                              </li>\
                          </ul>\
                      </span>\
                      <div class="blog-slider__title">' + event.name + '</div>\
                      <span class="blog-slider__code">\
                        <ul class="d-flex list-unstyled mt-auto">\
                          <li class="d-flex align-items-center">\
                            <i class="bi bi-geo-alt-fill" style="padding-right: 0.5rem;"></i>\
                            <small>' + event.location + '</small>\
                          </li>\
                          <li class="d-flex align-items-center">\
                            <i class="bi bi-calendar-date" style="padding-left: 1rem; padding-right: 0.5rem;"></i>\
                            <small> ' + formatedDate + ' </small>\
                          </li>\
                          <li class="d-flex align-items-center">\
                            <i class="bi bi-people" style="padding-left: 1rem; padding-right: 0.5rem;"></i>\
                            <small>' + devolverNumAsistentesEvento(event.id) + '/' + event.capacity + '</small>\
                          </li>\
                        </ul>\
                      </span>\
                      <div class="blog-slider__text">' + formatedDescriptionFilteredCards(description, txtToFilt) + '</div>\
                        <div class="justify-content-center mt-2 mb-2" align="center">\
                            <button type="button" class="btn btn-secondary me-1 pe-5 ps-5" id="event' + event.id + '" >Ver</button>\
                        </div>\
                      </div>\
                    </div>\
                  </div>\
                </div>\
              </div>'
        document.getElementById("listEvents").innerHTML += txt;

    }

    var formatedDescriptionFilteredCards = function (description, busq) {
        var tamDescripcion = description.length;
        var tam = busq.length;
        var index = -2;
        var inicio = 0;
        var toret = "";
        var aux_tolower = description.toLowerCase();
        var busq_tolower = busq.toLowerCase();
        if (aux_tolower.indexOf(busq_tolower) == -1) {
            return description;
        } else {
            do {
                index = aux_tolower.indexOf(busq_tolower, index + 1);
                if (index > -1) {
                    toret += description.substring(inicio, index) + "<b>" + description.substring(index, index + tam) + "</b> ";
                    inicio = index + tam + 1;
                }
            } while (index != -1);
            if (inicio < tamDescripcion) {
                toret += description.substring(inicio, tamDescripcion);
            }
            return toret;
        }
    }

    var insertCreateEventForm = function () {
        var formatedDate = formatedDateForm();
        document.getElementById('modalText').className = " ";
        document.getElementById('modalDialog').className = "modal-dialog modal-lg modal-fullscreen-sm-down ";
        var toInner = '<div class="mx-3 mt-3">\
    <form id="form-crear">\
        <h2 class="mb-3 text-center">Crear nuevo evento</h2>\
        <div class="row  mb-3">\
            <div class="col">\
                <div class="form-floating" id=eventNameFloat>\
                    <input type="text" class="form-control" id="eventName" placeholder="Nombre del Evento" required>\
                    <label for="eventName">Nombre del Evento</label>\
                    <small id="errorEventName" class="form-text" style="color: red; display: none;">El nombre del evento no puede estar vacío</small>\
                </div>\
            </div>\
            <div class="col-md-auto d-flex align-items-center">\
                <div class="form-check form-switch">\
                    <input class="form-check-input" type="checkbox" id="flexSwitchCheckDefault">\
                    <label class="form-check-label" for="flexSwitchCheckDefault" id="labelDestacado">No Destacado</label>\
                </div>\
            </div>\
          </div>\
        <div class="form-floating mb-3" id=eventDescriptionFloat>\
            <textarea cols="40" class="form-control" id="eventDescription" placeholder="Descripcion" required style="height: 150px"></textarea>\
            <label for="eventDescription">Descripcion</label>\
            <small id="errorEventDescription" class="form-text" style="color: red; display: none;">La descripción del evento no puede estar vacía</small>\
        </div>\
        <div class="row">\
            <div class="col-8">\
              <div class="form-floating mb-3" id="eventLocationFloat">\
                  <input type="text" class="form-control" id="eventLocation" placeholder="Ciudad" required>\
                  <label for="eventLocation">Ciudad</label>\
                  <small id="errorEventLocation" class="form-text" style="color: red; display: none;">La localización del evento no puede estar vacía</small>\
              </div>\
            </div>\
            <div class="col-4">\
              <div class="form-floating mb-3" id="eventDateFloat">\
                 <input type="datetime-local" class="form-control" id="eventDate" min="' + formatedDate + '" value="' + formatedDate + '" placeholder="Fecha" required>\
                  <label for="eventDate">Fecha</label>\
                  <small id="errorEventDate" class="form-text" style="color: red; display: none;">La fecha no puede ser del pasado</small>\
              </div>\
            </div>\
          <div class="col-8">\
             <div class="form-floating">\
              <select class="form-select" aria-label="Default select example" id="eventTag">\
                  <option value="deporte">Deporte</option>\
                  <option value="cine">Cine</option>\
                  <option value="teatro">Teatro</option>\
                  <option value="tv">Televisión</option>\
                  <option value="series">Series</option>\
                  <option value="libros">Libros</option>\
                  <option value="tiempo_libre">Tiempo-Libre</option>\
              </select>\
                <label for="eventTag">Etiqueta</label>\
            </div>\
             </div>\
             <div class="col-4">\
                <div class="form-floating">\
                    <select class="form-select" aria-label="Default select example" id="eventPeople">';
        for (i = 2; i <= 20; i++) {
            toInner += '<option value="' + i + '">' + i + '</option>';
        }
        toInner += '</select><label for="eventPeople">Aforo</label>\
                </div>\
            </div>\
        <div class="mb-3">\
            <label for="formFile" class="form-label">Imagen</label>\
            <input class="form-control" type="file" id="formFile" accept="image/*"><br>\
            <small id="errorFormFile" class="form-text" style="color: red; display: none;">La imagen no puede superar los 64KB</small>\
            <div id="imgContainer" align="center">\
            <img id="output" style="max-height:200px; max-width:300px; display: none;">\
        </div>\
        </div>\
        <div class="d-grid gap-2 col-6 mx-auto mt-3">\
            <button type="button" class="btn btn-lg btn-secondary btn-block" id="createEventSend">Crear</button>\
        </div>\
    </form>\
    </div>';
        document.getElementById('modalText').innerHTML = toInner;

        document.getElementById("formFile").oninput = function (input) {
            if(this.files[0].size > 64000){
                document.getElementById("errorFormFile").style.display = "block";
                document.getElementById("formFile").style.borderColor = "red";
                document.getElementById('imgContainer').innerHTML ='<img id="output" style="height:200px; width:200px; display: none;">';
                document.getElementById('output').style.display = "none"
            } else {
                document.getElementById("errorFormFile").style.display = "none";
                document.getElementById("formFile").style.borderColor = "grey";
                var input = input.target;
                var reader = new FileReader();
                reader.onload = function () {
                    var dataURL = reader.result;
                    var output = document.getElementById('output');
                    output.src = dataURL;
                    output.style.display = "block";
                };
                reader.readAsDataURL(input.files[0]);
            };
        };
        addListenerBotonSelecImportante();
    }

    var imagen64 = function getBase64Image(img) {
        var canvas = document.createElement("canvas");
        canvas.width = img.width;
        canvas.height = img.height;
        var ctx = canvas.getContext("2d");
        ctx.drawImage(img, 0, 0);
        var dataURL = canvas.toDataURL("image/png");
        var input = dataURL;
        var fields = input.split(',');
        var name = fields[0];
        var street = fields[1];
        return street;
    }

    var formatedDateForm = function () {
        var d = new Date();
        var mes = "";
        var dia = "";     
        var horas = "";
        var mins = "";

        if (parseInt(d.getMonth() + 1) >= 10) {
            mes = d.getMonth() + 1;
        } else {
            mes = "0" + parseInt(d.getMonth() + 1);
        }

        if (parseInt(d.getDate()) >= 10) {
            dia = d.getDate();
        } else {
            dia = "0" + parseInt(d.getDate());
        }
        
        if (parseInt(d.getHours()) >= 10) {
            horas = d.getHours();
        } else {
            horas = "0" + parseInt(d.getHours());
        }
        
        if (parseInt(d.getMinutes()) >= 10) {
            mins = d.getMinutes();
        } else {
            mins = "0" + parseInt(d.getMinutes());
        }

        var formatedDate = d.getFullYear() + "-" + mes + "-" + dia + "T" + horas + ":" + mins + ":00";
        return formatedDate;
    }

    var devolverNumAsistentesEvento = function (idEvento) {
        var apuntados;
        dao.numUsersInEvent(idEvento, function (numAsistentes) {
            apuntados = numAsistentes;
        },
                function () {
                    document.getElementById("modalText").className = "alert alert-danger"
                    document.getElementById('modalText').innerHTML = "No ha sido posible acceder al listado de asistentes";
                    document.getElementById('showModal').click();
                });
        return apuntados;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////LISTENERS SETTTERS/////////////////////////////////////////////////////////////////////

    var addViewButtonListener = function (event) {
        $('#event' + event.id).click(function () {
            showEvent(event);
        });
    }

    var addBackListener = function () {
        $('#back').click(function () {
            self.init(0);
        });
    }

    var addSearchListener = function () {
        $('#searchText').keypress(function (e) {
            var code = (e.keyCode ? e.keyCode : e.which);
            if (e.keyCode === 13) {
                txtToFilt = document.getElementById('searchText').value;
                if (txtToFilt.length < 2) {
                    document.getElementById("modalText").className = "alert alert-danger"
                    document.getElementById('modalText').innerHTML = "El texto a buscar debe tener 2 caracteres o más";
                    document.getElementById('showModal').click();
                    txtToFilt = "none";
                } else {
                    self.init(-1);
                    document.getElementById('start').scrollIntoView();
                }
            }
        });
        $('#searchButton').click(function () {
            txtToFilt = document.getElementById('searchText').value;
            if (txtToFilt.length < 2) {
                document.getElementById("modalText").className = "alert alert-danger"
                document.getElementById('modalText').innerHTML = "El texto a buscar debe tener 2 caracteres o más";
                document.getElementById('showModal').click();
                txtToFilt = "none";
            } else {
                self.init(-1);
                document.getElementById('start').scrollIntoView();
            }
        });
    }

    var addButtonsListeners = function () {
        $('#plusPage').click(function () {
            self.init(9);
            document.getElementById('tittleEvents').scrollIntoView();
        });
        $('#minusPage').click(function () {
            self.init(-9);
            document.getElementById('tittleEvents').scrollIntoView();
        });
        $('#inicio').click(function () {
            txtToFilt = "none"
            self.init(-1);
            document.getElementById('start').scrollIntoView();
        });
    }

    var addLogInListeners = function () {
        $('#signUp').click(function () {
            insertLoginFormContent();
            document.getElementById('modalText').className = "alert";
            document.getElementById('showModal').click();

            $('#enterButton').click(function () {
                usersDAO.doLogin(document.getElementById('login').value, document.getElementById('password').value);
                self.init(-1);
            });

            $('#password').keypress(function (e) {
                var code = (e.keyCode ? e.keyCode : e.which);
                if (e.keyCode === 13) {
                    usersDAO.doLogin(document.getElementById('login').value, document.getElementById('password').value);
                    self.init(-1);
                }
            });
        });

        $('#logOut').click(function () {
            usersDAO.doLogout();
            self.init(-1);
        });
    }

    var addListenerUserEventRegister = function () {
        $('#joinButton').click(function () {
            var idEvento = document.getElementById("idEvento").value;
            var user_id = localStorage.getItem('user_id');
            dao.joinInEvent(idEvento, user_id, function () {
                document.getElementById("modalText").className = "alert alert-success"
                document.getElementById('modalText').innerHTML = "Se ha inscrito correctamente";
                document.getElementById('showModal').click();
                var txt = '<input type="hidden" id="idEvento" value="' + idEvento + '">';
                txt += '<button id="unJoinButton" type="button" class="btn btn-secondary me-1"><i class="bi bi-calendar-x-fill" style="margin-right: 1rem;"></i>Cancelar asistencia</button></button></input>';
                document.getElementById('joinButtonContainer').innerHTML = txt;
                var etiqueta = document.getElementById('etiquetaCapacidad').innerHTML;
                var a = parseInt(etiqueta.substring(0, etiqueta.indexOf("/")));
                var b = (a + 1) + "/" + etiqueta.substring(etiqueta.indexOf("/") + 1, etiqueta.length);
                document.getElementById('etiquetaCapacidad').innerHTML = b;
                addListenerCancelUserEventRegister();
            },
                    function () {
                        document.getElementById("modalText").className = "alert alert-danger"
                    document.getElementById('modalText').innerHTML = "No ha sido posible inscribirse en el evento.";
                    document.getElementById('showModal').click();
                    });
        });
    }

    var addListenerCancelUserEventRegister = function () {
        $('#unJoinButton').click(function () {
            var idEvento = document.getElementById("idEvento").value;
            var user_id = localStorage.getItem('user_id');
            dao.deleteAssistance(user_id, idEvento, function () {
                document.getElementById("modalText").className = "alert alert-success"
                document.getElementById('modalText').innerHTML = "Se ha cancelado su asistencia correctamente";
                document.getElementById('showModal').click();
                var txt = '<input type="hidden" id="idEvento" value="' + idEvento + '">';
                txt += '<button id="joinButton" type="button" class="btn btn-secondary me-1"><i class="bi bi-calendar-check-fill" style="margin-right: 1rem;"></i>Inscribirse</button></input>';
                document.getElementById('joinButtonContainer').innerHTML = txt;
                var etiqueta = document.getElementById('etiquetaCapacidad').innerHTML;
                var a = parseInt(etiqueta.substring(0, etiqueta.indexOf("/")));
                var b = (a - 1) + etiqueta.substring(etiqueta.indexOf("/"), etiqueta.length);
                document.getElementById('etiquetaCapacidad').innerHTML = b;
                addListenerUserEventRegister();
            },
                    function () {
                        document.getElementById("modalText").className = "alert alert-danger"
                    document.getElementById('modalText').innerHTML = "No ha sido posible cancelar tu asistencia al evento.";
                    document.getElementById('showModal').click();
                    });

        });
    }

    var addCreateEventListener = function () {
        $('#createEvent').click(function () {
            insertCreateEventForm();
            document.getElementById('showModal').click();
            addListenerCreateEvent();
            addListenersFormParams();
        });
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////QUERIES TO EVENT DAO ////////////////////////////////////////////////////////////////////////////
    var deleteEventListener = function () {
         $('#deleteEventButton').click(function () {
            document.getElementById("modalText").className = "";
            document.getElementById('modalText').innerHTML ='<div class="d-flex justify-content-center"><h4>¿Estás seguro de que quieres borrar el evento?</h4></div>\
                <div class="d-flex justify-content-center mt-3 mb-2">\
                    <button type="button" class="btn btn-secondary me-3" id="deleteEventButtonConfirmed"><i class="bi bi-check-square me-1"></i>Aceptar</button>\
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal"><i class="bi bi-x-square me-1"></i>Cancelar</button>\
                </div>';
            document.getElementById('showModal').click();
                
            $('#deleteEventButtonConfirmed').click(function () {
                var idEvento = document.getElementById("idEvento").value;
                var user_id = localStorage.getItem('user_id');
                dao.deleteEvent(user_id, idEvento, function () {
                    self.init(-1);
                    document.getElementById("modalText").className = "alert alert-success"
                    document.getElementById('modalText').innerHTML = "El evento se ha eliminado correctamente";
                },
                function () {
                    document.getElementById("modalText").className = "alert alert-danger"
                    document.getElementById('modalText').innerHTML = "No ha sido posible eliminar el evento.";
                });
            });
                
        });
        
    }

    var addListenerBotonSelecImportante = function () {
        $('#flexSwitchCheckDefault').click(function () {
            if (document.getElementById('labelDestacado').innerHTML == "Destacado") {
                document.getElementById('labelDestacado').innerHTML = "No destacado";
            } else {
                document.getElementById('labelDestacado').innerHTML = "Destacado";
            }
        });
    }

    var addListenersFormParams = function () {
        $("#eventNameFloat").focusout(function () {
            var nombre = document.getElementById("eventName").value;
            if (nombre.length == 0) {
                document.getElementById("errorEventName").style.display = "block";
                document.getElementById("eventName").style.borderColor = "red";
            } else {
                document.getElementById("errorEventName").style.display = "none";
                document.getElementById("eventName").style.borderColor = "grey";
            }
        });


        $("#eventDescriptionFloat").focusout(function () {
            var nombre = document.getElementById("eventDescription").value;
            if (nombre.length == 0) {
                document.getElementById("errorEventDescription").style.display = "block";
                document.getElementById("eventDescription").style.borderColor = "red";
            } else {
                document.getElementById("errorEventDescription").style.display = "none";
                document.getElementById("eventDescription").style.borderColor = "grey";
            }
        });

        $("#eventLocationFloat").focusout(function () {
            var nombre = document.getElementById("eventLocation").value;
            if (nombre.length == 0) {
                document.getElementById("errorEventLocation").style.display = "block";
                document.getElementById("eventLocation").style.borderColor = "red";
            } else {
                document.getElementById("errorEventLocation").style.display = "none";
                document.getElementById("eventLocation").style.borderColor = "grey";
            }
        });

        $("#eventDateFloat").focusout(function () {
            var flag = false;
            var formatedDate = formatedDateForm();
            var formDate = document.getElementById("eventDate").value;
            if (formatedDate.substring(0, 10) === formDate.substring(0, 10)) {
                if (parseInt(formDate.substring(11, 13)) < parseInt(formatedDate.substring(11, 13))) {
                    flag = true;
                }
                if (parseInt(formDate.substring(11, 13)) === parseInt(formatedDate.substring(11, 13))) {
                    if (parseInt(formDate.substring(14, 16)) <= parseInt(formatedDate.substring(14, 16))) {
                        flag = true;
                    }
                }
            }

            if (flag) {
                document.getElementById("errorEventDate").style.display = "block";
                document.getElementById("eventDate").style.borderColor = "red";
            } else {
                document.getElementById("errorEventDate").style.display = "none";
                document.getElementById("eventDate").style.borderColor = "grey";
            }
        });
    }

    var addListenerCreateEvent = function () {
        $('#createEventSend').click(function () {
            var flag = false;
            var formatedDate = formatedDateForm();
            var formDate = document.getElementById("eventDate").value;
            if (formatedDate.substring(0, 10) === formDate.substring(0, 10)) {
                if (parseInt(formDate.substring(11, 13)) < parseInt(formatedDate.substring(11, 13))) {
                    flag = true;
                    document.getElementById("errorEventDate").style.display = "block";
                    document.getElementById("eventDate").style.borderColor = "red";
                }
                if (parseInt(formDate.substring(11, 13)) === parseInt(formatedDate.substring(11, 13))) {
                    if (parseInt(formDate.substring(14, 16)) <= parseInt(formatedDate.substring(14, 16))) {
                        flag = true;
                        document.getElementById("errorEventDate").style.display = "block";
                        document.getElementById("eventDate").style.borderColor = "red";
                    }
                }
            }
            var nombreEvento = document.getElementById("eventName").value;
            var descripcionEvento = document.getElementById("eventDescription").value;
            var ciudadEvento = document.getElementById("eventLocation").value;
            
            if (nombreEvento.length == 0) {
                flag = true;
                document.getElementById("errorEventName").style.display = "block";
                document.getElementById("eventName").style.borderColor = "red";
            }
            
             if (descripcionEvento.length == 0) {
                flag = true;
                document.getElementById("errorEventDescription").style.display = "block";
                document.getElementById("eventDescription").style.borderColor = "red";
            }
            
            if (ciudadEvento.length == 0) {
                flag = true;
                document.getElementById("errorEventLocation").style.display = "block";
                document.getElementById("eventLocation").style.borderColor = "red";
            }

            if (!flag) {
                var user_id = localStorage.getItem('user_id');
                var inputFecha = document.getElementById("eventDate").value;
                var fecha = inputFecha.substring(0, inputFecha.indexOf("T"));
                var hora = inputFecha.substring(inputFecha.indexOf("T") + 1, inputFecha.length);
                var fechaEvento = fecha + " " + hora;
                if (fechaEvento.length == 16) {
                    fechaEvento += ":00";
                }
                var aforoEvento = document.getElementById("eventPeople").value;
                var etiquetaEvento = document.getElementById("eventTag").value;
                var labelEvento = document.getElementById("labelDestacado").innerHTML;
                var imagenEvento = imagen64(document.getElementById("output"));
                if (document.getElementById("output").src === "") {
                    imagenEvento = "none";
                }
                var importanteEvento = "1";
                if (labelEvento === "Destacado") {
                    importanteEvento = true;
                } else {
                    importanteEvento = false;
                }
                dao.addEvent(user_id, nombreEvento, descripcionEvento, ciudadEvento, fechaEvento, aforoEvento, etiquetaEvento, importanteEvento, imagenEvento, function () {
                    self.init(-1);
                    document.getElementById("modalText").className = "alert alert-success"
                    document.getElementById('modalText').innerHTML = "El evento se ha añadido correctamente";
                },
                        function () {
                              document.getElementById("modalText").className = "alert alert-danger"
                              document.getElementById('modalText').innerHTML = "No ha sido posible insertar el evento";
                        });
               // document.getElementById('arriba').scrollIntoView();
            } 
        });
    }

    var getNumNormalEvents = function () {
        dao.numNormalEvents(function (nEvents) {
            numEvents = nEvents;
        },
                function () {
                    document.getElementById("modalText").className = "alert alert-danger"
                    document.getElementById('modalText').innerHTML = "No ha sido posible contar el numero de eventos.";
                    document.getElementById('showModal').click();
                });
    }

    var getNumFilterNormalEvents = function () {
        dao.numFilterNormalEvents(txtToFilt, function (nEvents) {
            numEvents = nEvents;
        },
                function () {
                    document.getElementById("modalText").className = "alert alert-danger"
                    document.getElementById('modalText').innerHTML = "No ha sido posible contar el numero de eventos filtrados.";
                    document.getElementById('showModal').click();
                });
    }

    var getListNormalEvents = function () {
        dao.listNormalEvents(actualPage, function (event) {
            $.each(event, function (key, event) {
                addCardEvent(event);
                arrayEvents[contEvents] = event;
                contEvents++;
            });
        },
                function () {
                    document.getElementById("modalText").className = "alert alert-danger"
                    document.getElementById('modalText').innerHTML = "No ha sido posible contar el numero de eventos normales.";
                    document.getElementById('showModal').click();
                });

    }

    var getListFilterNormalEvents = function () {
        dao.listFilterNormalEvents(actualPage, txtToFilt, function (event) {
            $.each(event, function (key, event) {
                showListEventsSearch(event);
                arrayEvents[contEvents] = event;
                contEvents++;
            });
        },
                function () {
                    document.getElementById("modalText").className = "alert alert-danger"
                    document.getElementById('modalText').innerHTML = "No has sido posible acceder al listado de eventos normales filtrados.";
                    document.getElementById('showModal').click();
                });
    }

    var getListImportantEvents = function () {
        dao.listImportantEvents(function (event) {
            $.each(event, function (key, event) {
                if (contEvents < 1) {
                    firstSliderItem(event);
                    document.getElementById("indicators").innerHTML += '<button type="button" data-bs-target="#myCarousel" data-bs-slide-to="0" class="active" aria-current="true" aria-label="Slide 1"></button>';
                } else {
                    sliderItem(event);
                    document.getElementById("indicators").innerHTML += '<button type="button" data-bs-target="#myCarousel" data-bs-slide-to="' + contEvents + '" aria-label="Slide ' + contEvents + '"></button>';
                }
                arrayEvents[contEvents] = event;
                contEvents++;
            });
        },
                function () {
                    document.getElementById("modalText").className = "alert alert-danger"
                    document.getElementById('modalText').innerHTML = "No has sido posible acceder al listado de eventos importantes.";
                    document.getElementById('showModal').click();
                });
    }


    return EventView;
})();