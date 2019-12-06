import React, { Component } from 'react'
import Button from '../../components/atoms/Button';

import '../../styles/pages/Landing.css';

const Landing = () => {
    return(
        <section className="landing">
            <div className="container-fluid">
                <div className="row container">
                    <div className="col-sm-12 col-md-6 col-lg-6">
                        <h5 className="landing-title">LSCH App</h5>
                        <div className="landing-des">Una aplicación que te entrega con todo el cariño, la lengua de señas chilena (LSCH).</div>
                        <Button title="Leer más" background="white" className="landing-btn"/>
                        <div className="links">
                            <a href="https://play.google.com/store?hl=es_419">
                                <img className="google-play-btn" src="play.png"/>
                            </a>
                            <a href="https://www.apple.com/cl/ios/app-store/">
                                <img className="app-store-btn" src="appstore.png"/>
                            </a>
                        </div>
                    </div>
                    <div className="col-sm-12 col-md-6 col-lg-6">
                        <img src="./group.png" alt="lsch" className="landing-img"/>
                    </div>
                </div>
            </div>
        </section>
    )
}

export default Landing;