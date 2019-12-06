import React, { Component, Fragment } from 'react'
import { Route, NavLink } from 'react-router-dom';
import '../styles/Navbar.css'

class Navbar extends Component {
    render(){
        return (
            <nav className="navbar navbar-expand-md navbar-light sticky-top" id="toolbar">
                <div className="container-fluid">
                <NavLink className="nav-link" to="/"><img src="./favicon.png" height="24px"/></NavLink>
                    <button className="navbar-toggler leftNavbarToggler order-first" type="button" data-toggle="collapse" data-target="#navbarResponsive">
                        <span className="navbar-toggler-icon"></span>
                    </button>
                    <div className="collapse navbar-collapse" id="navbarResponsive">
                        <ul className="navbar-nav ml-auto">
                            <Route path="/" exact render={ () => {
                                return(
                                    <Fragment>
                                        <NavLink className="nav-link" setactiveclassname={'active'} to="/about">Acerca de</NavLink>
                                        {/*<NavLink className="nav-link" setactiveclassname={'active'} to="/case-studies">Estudios de caso</NavLink>*/}
                                        <NavLink className="nav-link" setactiveclassname={'active'} to="/download">Descargar</NavLink>
                                        <NavLink className="nav-link" setactiveclassname={'active'} to="/contact">Contacto</NavLink>
                                    </Fragment>
                                )
                            }}
                            />
                            <Route path="/(about|download|contact|case-studies)" render= { () => {
                                return(
                                    <Fragment>
                                        <NavLink className="nav-link" setactiveclassname={'active'} to="/#" exact>Inicio</NavLink>
                                        <NavLink className="nav-link" setactiveclassname={'active'} to="/about">Acerca de</NavLink>
                                        {/*<NavLink className="nav-link" setactiveclassname={'active'} to="/case-studies">Estudios de caso</NavLink>*/}
                                        <NavLink className="nav-link" setactiveclassname={'active'} to="/download">Descargar</NavLink>
                                        <NavLink className="nav-link" setactiveclassname={'active'} to="/contact">Contacto</NavLink>
                                    </Fragment>
                                )
                            }}
                            />
                        </ul>    
                    </div>
                </div>
            </nav>
        )
    }
}

export default Navbar;
