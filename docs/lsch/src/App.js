import React, { Fragment } from 'react';
import { BrowserRouter as Router, Route, Switch } from 'react-router-dom';

import Navbar from './components/Navbar';
import Footer from './components/Footer';

import Home from './pages/Home';
import About from './pages/About';
import Contact from './pages/Contact';
import CaseStudies from './pages/CaseStudies';
import Page404 from './pages/Page404';

import './styles/main.css'

const App = () => {
    return(
        <Router>
            <Fragment>
                <Navbar/>
                <div className="fragment">
                <Switch>
                    <Route path="/" exact component={Home} />
                    <Route path="/case-studies" exact component={CaseStudies}/>
                    <Route path="/about" exact component={About} />
                    <Route path="/contact" exact component={Contact} />
                    <Route component={Page404} />
                </Switch>   
                </div>
                <Route path="/(|case-studies|about|contact)" component={Footer}/>
            </Fragment>
        </Router>
    )
}
export default App;