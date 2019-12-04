import React, { Component } from 'react'
import Button from '../components/atoms/Button'

import '../styles/pages/Landing.css';

const Landing = () => {
    return(
        <section className="landing">
            <div className="container-fluid">
                <div className="row container">
                    <div className="col-sm-12 col-md-12 col-lg-6">
                        <h5 className="landing-title">LSCH App</h5>
                        <div className="landing-des">Temporibus omnis quis non quis. Dolores consequatur dolore autem asperiores qui saepe illo quae. Quasi quo praesentium quia illum placeat repellat qui. Odio ad inventore nobis autem nostrum non.</div>
                        <Button title="Leer Mas" background="white"/>
                    </div>
                    <div className="col-sm-12 col-md-12 col-lg-6">
                        <img src="./lsch.png" alt="lsch"/>
                    </div>
                </div>
            </div>
        </section>
    )
}

export default Landing;