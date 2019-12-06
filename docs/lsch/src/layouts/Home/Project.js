import React from 'react';

import Button from '../../components/atoms/Button';

import '../../styles/pages/Project.css';

const Project = () => {
    return(
        <div className="project">
            
            <div className="container-fluid">
                <div className="row">
                    <div className="col-xl-6 col-lg-6 col-md-12 col-sm-12 container">
                        <img src="./lsch-2.png" alt="lsch" className="project-img"/>
                    </div>
                    <div className="col-xl-6 col-lg-6 col-md-12 col-sm-12 container">
                        <div className="content-container">
                            <div className="project-title">
                                Hecho con <img alt="❤" height="36px" className="imga" src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAEgAAABICAMAAABiM0N1AAAARVBMVEVHcEwAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADoEiTvaVCTCxfrMTNYBg3vXUrLDx+2DhwuAwfdESIUAQN6CRO86aZfAAAACnRSTlMApIHxuw8vVfvbEUJr3wAAAfRJREFUWMPt1cm2gyAMANCqICJFxOn/P/UZUMsgFtDVO83CTeAaIuDr9YtffAKTEtE1UEGwnyt0rvRzTtRF1bZt0+oHImaOoC0Dz6qorxjUOkEPilA3h4JU2bR+FGoRuDhJNeV5c7axs+CDlANfxq2o9cX1Vs646JyY9QqLk1ZhvaxxYEdIsUmbI+QnN+jXIF9S9cycWaGHU+q+AoKrqgqvP2qsZE5MYm+ImNycVG9x+lRDn0dv7BqbJE5SE0iN/e2gQbNkLCSJ05SE1SFrr8FgzlhIEoEUh3nEKSg0eJUuUnZJGOD9o3R937HYGGAmtlY27s57jXhptNYGe2jZMm8V0dBi7SVqrCwRgrXRA4KrQ+ZBcp1aHRB0jOVBDOY+D8H5mPKgCU6J1ey9R/076ftLq9nIOCAaipa4tbVL44R07yRJWDdJDWd/skuKkyY4/8ZFUhlbO0larG2k13aU1MVLqiDzjsSVeY/ES9ChBntXNk+VuH9pY5ohKYc6P6S6SpaUU3n/bdIkSsppiP+rTZSCTlDqU52Q1Cc7AalLdzwJTkuW40uM5TlRUpQTIUU6X6Vo54uU4FxKSc6FlOgEpWQnIGU4p1KWcyJlOp6U7TjSDceSbjmGdNM5pNvOLt13PtJdZ5fuO1p6wgHpGWeVHnJ+8X/jD+opa3AehG5eAAAAAElFTkSuQmCC"></img>
                            </div>
                            <div className="project-des">
                                LSCH App fue creada con la intención de mejorar la comunicación de la lengua de señas chilena, ofreciendote en la palma de tu mano todas las palabras con su interpretación, ofrecidas por el departamento de educación diferencial de la Universidad Metropolitada de las Ciencias de la Educación (UMCE) de Santiago de Chile.
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    )
}

export default Project;