import React from 'react';
import Header from './albums/header';
import PhotoGrid from './albums/photoGrid';
import { Box } from '@mui/material';


export default function Albums() {
  return (
    <div>
      <Header />
      <Box sx={{ p: 3 }}>
        <PhotoGrid />
      </Box>
    </div>
  );
}
