import React from 'react';

import Landing from '../layouts/Home/Landing';
import Project from '../layouts/Home/Project';

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