import React from "react";

import "../styles/Footer.css";

const Footer = () => {
  return (
    <footer className="footer">
      <div class="container py-5">
        <div class="row">
          <div class="col-12 col-md">
            <img src="favicon.png" />
            <small class="d-block mb-3 text-muted">© 2017-2019</small>
          </div>
          <div class="col-6 col-md">
            <h5>Información</h5>
            <ul class="list-unstyled text-small">
              <li>
                <a class="text-muted" href="#">
                  UMCE
                </a>
              </li>
              <li>
                <a class="text-muted" href="#">
                  Código
                </a>
              </li>
              <li>
                <a class="text-muted" href="#">
                  Equipo
                </a>
              </li>
            </ul>
          </div>
          <div class="col-6 col-md">
            <h5>Desarrolladores</h5>
            <ul class="list-unstyled text-small">
              <li>
                <a class="text-muted" href="#">
                  API
                </a>
              </li>
              <li>
                <a class="text-muted" href="#">
                  Documentación
                </a>
              </li>
              <li>
                <a class="text-muted" href="#">
                  Código
                </a>
              </li>
            </ul>
          </div>
          <div class="col-6 col-md">
            <h5>Contacto</h5>
            <ul class="list-unstyled text-small">
              <li>
                <a class="text-muted" href="#">
                  Correo
                </a>
              </li>
              <li>
                <a class="text-muted" href="#">
                  Instagram
                </a>
              </li>
              <li>
                <a class="text-muted" href="#">
                  Facebook
                </a>
              </li>
              <li>
                <a class="text-muted" href="#">
                  Github
                </a>
              </li>
            </ul>
          </div>
          <div class="col-6 col-md">
            <h5>Legal</h5>
            <ul class="list-unstyled text-small">
              <li>
                <a class="text-muted" href="#">
                  Licencia
                </a>
              </li>
              <li>
                <a class="text-muted" href="#">
                  Condiciones de Uso
                </a>
              </li>
              <li>
                <a class="text-muted" href="#">
                  Términos
                </a>
              </li>
            </ul>
          </div>
        </div>
      </div>
    </footer>
  );
};

export default Footer;
