import React from 'react';

import '../../styles/atoms/Button.css';

const Button = (props) => {
    console.log(props.width)
    return ( 
        <div className="btn" style={{width: `${props.width}`, background: `${props.background}` }}>
            { props.title }
        </div>
    )
}

export default Button;