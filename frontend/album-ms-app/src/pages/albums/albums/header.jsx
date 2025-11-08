import React from 'react';
import { Link } from 'react-router-dom';
import { useLocation } from 'react-router-dom';
import { Button, AppBar, Toolbar, Typography } from '@mui/material';
import { fetchDeleteDataWithAuth } from 'client/client';
const Header = () => {
  const location = useLocation();
  const queryParams = new URLSearchParams(location.search);
  const id = queryParams.get('id');

  const handleDelete = () => {
    const isConfirmed = window.confirm('Are you sure you want to delete the Album?');
    if (isConfirmed) {
      console.log('Item deleted!');
      fetchDeleteDataWithAuth('/album/' + id + '/delete').then((res) => {
        console.log(res);
        window.location.href = '/';
      });
    } else {
      console.log('Delete operation cancelled');
    }
  };
  return (
    <AppBar position="static">
      <Toolbar>
        <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
          Photo Gallery
        </Typography>
        <Button
          component={Link}
          to={`/album/edit?id=${id}`}
          color="inherit"
          variant="contained"
          sx={{ mr: 2, backgroundColor: '#799edc', '&:hover': { backgroundColor: '#2f6ad0' } }}
        >
          Edit album
        </Button>
        <Button
          component={Link}
          to={`/album/upload?id=${id}`}
          color="inherit"
          variant="contained"
          sx={{ mr: 2, backgroundColor: '#4CAF50', '&:hover': { backgroundColor: '#388E3c' } }}
        >
          Upload photos
        </Button>
        <Button
          onClick={handleDelete}
          color="inherit"
          variant="contained"
          sx={{ mr: 2, backgroundColor: '#F44336', '&:hover': { backgroundColor: '#D32F2F' } }}
        >
          Delete album
        </Button>
      </Toolbar>
    </AppBar>
  );
};

export default Header;
