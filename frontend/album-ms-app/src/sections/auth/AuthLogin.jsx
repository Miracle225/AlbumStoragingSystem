import { TextField, Button, Container } from '@mui/material';
import PropTypes from 'prop-types';
import React, { useState } from 'react';
import { fetchPostData } from '../../client/client';
import { useNavigate } from 'react-router-dom';
import { useEffect } from 'react';
// import { Link as RouterLink } from 'react-router-dom';

// ============================|| JWT - LOGIN ||============================ //

export default function AuthLogin({ isDemo = false }) {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [errors, setErrors] = useState({ email: '', password: '' });
  const navigate = useNavigate();
  const [loginError, setLoginError] = useState('');

  useEffect(() => {
    const isLoggedIn = localStorage.getItem('token');
    if (isLoggedIn) {
      navigate('/albums');
      window.location.reload();
    }
  }, []);
  const validateEmail = () => {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
  };
  const validatePassword = () => {
    return password.length >= 6 && password.length <= 15;
  };
  const handleLogin = async () => {
    setErrors({ email: '', password: '' });

    if (!validateEmail()) {
      setErrors((prevErrors) => ({ ...prevErrors, email: 'Invalid email format' }));
      return;
    }
    if (!validatePassword()) {
      setErrors((prevErrors) => ({ ...prevErrors, password: 'Password nust be at least 6 characters' }));
      return;
    }
    fetchPostData('/auth/token', { email, password })
      .then((response) => {
        const { token } = response.data;
        setLoginError('');
        localStorage.setItem('token', token);
        navigate('/albums');
        window.location.reload();
      })
      .catch((error) => {
        console.error('Login error:', error);
        setLoginError('An error occured during login');
      });
  };
  return (
    <Container component="main" maxWidth="xs">
      <TextField
        variant="outlined"
        margin="normal"
        fullWidth
        label="email"
        value={email}
        onChange={(e) => setEmail(e.target.value)}
        error={!!errors.email}
        helperText={errors.email}
      ></TextField>
      <TextField
        variant="outlined"
        margin="normal"
        fullWidth
        label="password"
        value={password}
        onChange={(e) => setPassword(e.target.value)}
        error={!!errors.password}
        helperText={errors.password}
      ></TextField>
      <Button variant="contained" color="primary" fullWidth onClick={handleLogin}>
        Login
      </Button>
      {loginError && <p style={{ color: 'red' }}>{loginError}</p>}
    </Container>
  );
}

AuthLogin.propTypes = { isDemo: PropTypes.bool };
