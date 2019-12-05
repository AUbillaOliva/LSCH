import React from 'react';

import Button from '../components/atoms/Button';

import '../styles/pages/Project.css';

const Project = () => {
    return(
        <div className="project">
            <div className="carousel slide" data-ride="carousel" id="carouselControls">
                <div className="carousel-inner">
                    <div className="carousel-item active">
                        <div className="container-fluid">
                            <div className="row">
                                <div className="col-lg-6 col-md-12 col-sm-12 container">
                                    <img src="https://github.com/AUbillaOliva/LSCH/blob/master/docs/lsch/public/lsch.png?raw=true" alt="lsch" className="project-img"/>
                                </div>
                                <div className="col-lg-6 col-md-12 col-sm-12 container">
                                    <div class="project-title">
                                        Hecho con <img alt="❤" height="36px" className="imga" src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAEgAAABICAMAAABiM0N1AAAARVBMVEVHcEwAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADoEiTvaVCTCxfrMTNYBg3vXUrLDx+2DhwuAwfdESIUAQN6CRO86aZfAAAACnRSTlMApIHxuw8vVfvbEUJr3wAAAfRJREFUWMPt1cm2gyAMANCqICJFxOn/P/UZUMsgFtDVO83CTeAaIuDr9YtffAKTEtE1UEGwnyt0rvRzTtRF1bZt0+oHImaOoC0Dz6qorxjUOkEPilA3h4JU2bR+FGoRuDhJNeV5c7axs+CDlANfxq2o9cX1Vs646JyY9QqLk1ZhvaxxYEdIsUmbI+QnN+jXIF9S9cycWaGHU+q+AoKrqgqvP2qsZE5MYm+ImNycVG9x+lRDn0dv7BqbJE5SE0iN/e2gQbNkLCSJ05SE1SFrr8FgzlhIEoEUh3nEKSg0eJUuUnZJGOD9o3R937HYGGAmtlY27s57jXhptNYGe2jZMm8V0dBi7SVqrCwRgrXRA4KrQ+ZBcp1aHRB0jOVBDOY+D8H5mPKgCU6J1ey9R/076ftLq9nIOCAaipa4tbVL44R07yRJWDdJDWd/skuKkyY4/8ZFUhlbO0larG2k13aU1MVLqiDzjsSVeY/ES9ChBntXNk+VuH9pY5ohKYc6P6S6SpaUU3n/bdIkSsppiP+rTZSCTlDqU52Q1Cc7AalLdzwJTkuW40uM5TlRUpQTIUU6X6Vo54uU4FxKSc6FlOgEpWQnIGU4p1KWcyJlOp6U7TjSDceSbjmGdNM5pNvOLt13PtJdZ5fuO1p6wgHpGWeVHnJ+8X/jD+opa3AehG5eAAAAAElFTkSuQmCC"></img>
                                    </div>
                                    <div className="project-des">
                                        LSCH App fue creada con la intención de mejorar la comunicación de la lengua de señas chilena, ofreciendote en la palma de tu mano todas las palabras con su interpretación, ofrecidas por el departamento de educación diferencial de la Universidad Metropolitada de las Ciencias de la Educación (UMCE) de Santiago de Chile.
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    {/*<div className="carousel-item">
                        <div className="container-fluid">
                            <div className="row">
                                <div className="col-lg-6 col-md-12 col-sm-12">
                                    <div class="project-title">
                                        Conoce al autor
                                    </div>
                                    <div className="project-des">
                                        Soy Álvaro Ubilla Oliva, estudiante de Ingenieria civil en computación con mención en informatica. Actualmente estudio por mi propia cuenta diversas materias que puedan acelerar mi crecimiento profesional.
                                        A pesar de estar trabajando solo, estoy abierto a nuevas ideas que puedan mejorar la calidad de la aplicación.
                                    </div>
                                    <Button title="Conoce más aqui" background="white"/>
                                </div>
                                <div className="col-lg-6 col-md-12 col-sm-12">
                                    <img className="avatar" alt="afuo" src="https://avatars0.githubusercontent.com/u/29866563?s=460&v=4"/>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div className="carousel-item">
                        <div className="container-fluid">
                            <div className="row">
                                <div className="col-lg-6 col-md-12 col-sm-12">
                                    <div class="project-title">
                                        Manos a la obra
                                    </div>
                                    <div className="project-des">
                                        Puedes aportar al proyecto, ya que el código de este mismo es abierto. Puedes encontrar el repositiorio del projecto en el perfil de Github del autor.
                                    </div>
                                </div>
                                <div className="col-lg-6 col-md-12 col-sm-12">
                                </div>
                            </div>
                        </div>
                    </div>*/}
                </div>
                <div className="carousel-control-prev" href="#carouselControls" rol="button" data-slide="prev">
                    <span className="carousel-control-prev-icon" aria-hidden="true"></span>
                    <span className="sr-only">Prev</span>
                </div>
                <div className="carousel-control-next" href="#carouselControls" rol="button" data-slide="next">
                    <span className="carousel-control-next-icon" aria-hidden="true"></span>
                    <span className="sr-only">next</span>
                </div>
            </div>
        </div>
    )
}

export default Project;