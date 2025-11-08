import React, { useState } from 'react';
import Header from './albums/header';
import { Box, Button, Container, Grid, Paper, Typography, IconButton,CircularProgress } from '@mui/material';
import { AddCircleOutline, Close } from '@mui/icons-material';
import { useDropzone } from 'react-dropzone';
import { maxWidth, width } from '@mui/system';
import { useLocation, useNavigate } from 'react-router-dom';
import { fetchPostFileUploadWithAuth } from 'client/client';
const dropzoneSx = {
  border: (theme) => `2px dashed ${theme.palette.primary.main}`,
  borderRadius: 2,
  padding: 6,
  minHeight: 220,
  textAlign: 'center',
  cursor: 'pointer',
  display: 'flex',
  flexDirection: 'column',
  justifyContent: 'center',
  alignItems: 'center',
  backgroundColor: (theme) => (theme.palette.mode === 'dark' ? '#333' : '#fafafa'),
  transition: 'background-color 0.2s ease',
  '&:hover': {
    backgroundColor: (theme) => (theme.palette.mode === 'dark' ? '#444' : '#f0f0f0')
  }
};

const uploadedFileSx = {
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'space-between',
  padding: 2,
  marginTop: 2,
  border: (theme) => `1px solid ${theme.palette.secondary.main}`,
  borderRadius: 1
};
export default function FileUploadPage() {
  const [files, setFiles] = useState([]);
  const location = useLocation();
  const queryParams = new URLSearchParams(location.search);
  const id = queryParams.get('id');
  const navigate = useNavigate();
  const [processing, setProcessing] = useState(false);
  const onDrop = (acceptedFiles) => {
    setFiles((prevFiles) => [...prevFiles, ...acceptedFiles]);
  };
  const { getRootProps, getInputProps } = useDropzone({ onDrop });

  const removeFile = (index) => {
    setFiles((prevFiles) => prevFiles.filter((_, i) => i !== index));
  };

  const handleUpload = async () => {
    try {
      setProcessing(true);
      const formData = new FormData();
      files.forEach((file) => {
        formData.append('files', file);
      });
      fetchPostFileUploadWithAuth('/album/' + id + '/upload-photos',formData).then((res) => {
        console.log(res.data);
        navigate('/album/show?id='+id);
      });
      setFiles([]);
    } catch (error) {
      console.error('Error uploading files:', error.message);
    }
  };

  return (
    <div>
      <Header />
      <Container maxWidth="md">
        <Paper elevation={3} style={{ padding: '20px', marginTop: '20px' }}>
          <Grid container spacing={3} direction="column" alignItems="center">
            <Grid item xs={12}>
              <Typography variant="h4" align="center" gutterBottom>
                Photo Upload
              </Typography>
            </Grid>

            <Grid size={{xs:12}} style={{ width: '100%' }}>
              <Paper
                elevation={0}
                sx={{
                  ...dropzoneSx,
                  width: '80%',
                  maxWidth: 700,
                  mx: 'auto'
                }}
                {...getRootProps()}
              >
                <input {...getInputProps()} />
                <AddCircleOutline fontSize="large" color="primary" sx={{ mb: 1.5 }} />
                <Typography variant="h6">Drag and drop photos</Typography>
                <Typography variant="body1" color="textSecondary">
                  or click to select files
                </Typography>
              </Paper>
            </Grid>

            {files.length > 0 && (
              <Grid size={{xs:12}} style={{ width: '100%' }}>
                <Typography variant="h6" gutterBottom>
                  Uploaded Files:
                </Typography>
                <Box>
                  {files.map((file, index) => (
                    <Paper key={index} elevation={3} sx={uploadedFileSx}>
                      <Typography>{file.name}</Typography>
                      <IconButton onClick={() => removeFile(index)} color="secondary">
                        <Close />
                      </IconButton>
                    </Paper>
                  ))}
                </Box>
              </Grid>
            )}

            <Grid size={{xs:12}}>
              {processing ?(
                <Box textAlign="center">
                  <CircularProgress />
                  <Typography
                  variant="body2"
                  color="textSecondary"
                  marginTop="10px"
                  >
                    Uploading...
                  </Typography>
                </Box>
              ):(
                <Button
                variant="contained"
                color="primary"
                onClick={handleUpload}
                disabled={files.length===0}>
                  Upload Photo
                </Button>
              )}
                    </Grid>
          </Grid>
        </Paper>
      </Container>
    </div>
  );
}
