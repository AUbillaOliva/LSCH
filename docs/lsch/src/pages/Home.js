import React from 'react';

import Landing from '../layouts/Landing';
import Project from '../layouts/Project';

import '../styles/Home.css'

const Home = () => {
    return(
        <div className="home">
            <Landing/>
            <Project />
        </div>
    )
}

export default Home;