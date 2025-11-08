import React, { useEffect, useState } from 'react';
import { Card, CardContent, CardMedia, Grid, Typography, Tooltip } from '@mui/material';
import { useLocation } from 'react-router-dom';
import { fetchGetDataWithAuth, fetchGetDataWithAuthArrayBuffer, fetchDeleteDataWithAuth, fetchGetBlobDataWithAuth } from 'client/client';
import { Buffer } from 'buffer';
import Modal from '@mui/material/Modal';
import Button from '@mui/material/Button';
import Box from '@mui/material/Box';
const modalSx = {
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'center'
};

const modalMainSx = {
  position: 'absolute',
  top: '50%',
  left: '50%',
  transform: 'translate(-50%, -50%)',
  bgcolor: 'background.paper', 
  borderRadius: '10px',
  boxShadow: 24, 
  p: 4, 
  maxHeight: '90%',
  maxWidth: '90%',
  overflow: 'auto'
};

const closeButtonSx = {
  marginLeft: 'auto', 
  display: 'block' 
};

export default function PhotoGrid() {
  const [photos, setPhotos] = useState({});
  const location = useLocation();
  const queryParams = new URLSearchParams(location.search);
  const album_id = queryParams.get('id');
  const [albumInfo, setAlbumInfo] = useState({});
  const [open, setOpen] = useState(false);
  const [PhotoContent, setPhotoContent] = useState(null);
  const [PhotoDesc, setPhotoDesc] = useState(null);
  const [DownloadLink, setDownloadLink] = useState(null);

  const handleOpen = () => {
    setOpen(true);
  };
  const handleClose = () => {
    setOpen(false);
  };
  const handleView = (downloadLink, description) => {
    fetchGetDataWithAuthArrayBuffer('/album' + downloadLink).then((response) => {
      const buffer = Buffer.from(response.data, 'binary').toString('base64');
      setPhotoContent(buffer);
    });
    setDownloadLink(downloadLink);
    setPhotoDesc(description);
    handleOpen();
  };
  const handleDownload = (downloadLink) => {
    console.log(downloadLink);
    fetchGetBlobDataWithAuth('/album' + downloadLink)
      .then((response) => {
        console.log(response);
        const disposition = response.headers.get('Content-Disposition');
        const match = /filename="(.*)"/.exec(disposition);
        const filename = match ? match[1] : 'downloadedFile';
        const url = window.URL.createObjectURL(new Blob([response.data]));
        const link = document.createElement('a');
        link.href = url;
        link.setAttribute('download', filename);
        document.body.appendChild(link);
        link.click();
      })
      .catch((error) => {
        console.error('Error downloading photo:', error);
      });
  };
  const handleDelete = (photo_id) => {
    const isConfirmed = window.confirm('Are you sure you want to delete the Photo?');
    if (isConfirmed) {
      console.log('Item deleted! ' + photo_id);
      fetchDeleteDataWithAuth('/album/' + album_id + '/photos/' + photo_id + '/delete').then((res) => {
        console.log(res);
        window.location.reload();
      });
    } else {
      console.log('Delete operation cancelled');
    }
  };
  useEffect(() => {
    fetchGetDataWithAuth('/album/' + album_id).then((res) => {
      setAlbumInfo(res.data);
      const photoList = res.data.photos;
      photoList.forEach((photo) => {
        let thumbnailLink = photo.downloadLink.replace('/download-photo', '/download-thumbnail');
        fetchGetDataWithAuthArrayBuffer('/album' + thumbnailLink).then((response) => {
          const albumPhotoID = 'album_' + album_id + '_photo+' + photo.id;
          const buffer = Buffer.from(response.data, 'binary').toString('base64');
          const temp = {
            album_id: album_id,
            photo_id: photo.id,
            name: photo.name,
            description: photo.description,
            content: buffer,
            downloadLink: photo.downloadLink
          };
          setPhotos((prevPhotos) => ({ ...prevPhotos, [albumPhotoID]: temp }));
        });
      });
    });
  }, [album_id]);

 return (
    <div>
      <Modal
        open={open}
        onClose={handleClose}
        aria-labelledby="modal-modal-title"
        aria-describedby="modal-modal-description"
        sx={modalSx}
      >
        <Box sx={modalMainSx}>
          <img src={'data:image/jpeg;base64,' + PhotoContent} alt={PhotoDesc} style={{ width: '100%', height: 'auto' }} />
          <Button onClick={() => handleDownload(DownloadLink)}>Download Photo</Button>
          <Button onClick={handleClose} sx={closeButtonSx}>
            Close
          </Button>
        </Box>
      </Modal>
      <Typography variant="h4" gutterBottom>
        {albumInfo.name}
      </Typography>
      <Typography variant="subtitle1" gutterBottom>
        {albumInfo.description}
      </Typography>
      <Grid container spacing={2}>
        {Object.keys(photos).map((key) => (
          <Grid key={key} size ={{xs:12, sm:6, md:4, lg:3}}>
            <Card>
              <Tooltip title={photos[key]['description'] || 'No description'}>
                <CardMedia
                  component="img"
                  height="200"
                  image={'data:image/jpeg;base64,' + photos[key]['content']}
                  alt={photos[key]['description']}
                />
              </Tooltip>
              <CardContent>
                <Typography variant="subtitle1" noWrap>
                  {photos[key]['name']}
                </Typography>
                <a
                  href="#"
                  onClick={() => handleView(photos[key]['downloadLink'], photos[key]['description'])}
                  style={{ textDecoration: 'none' }}
                >
                  {' '}
                  View{' '}
                </a>
                |
                <a
                  href={`/photo/edit?album_id=${album_id}&photo_id=${photos[key]['photo_id']}&photo_name=${photos[key]['name']}&photo_desc=${photos[key]['description']}`}
                  style={{ textDecoration: 'none' }}
                >
                  {' '}
                  Edit{' '}
                </a>
                |
                <a href="#" onClick={() => handleDownload(photos[key]['downloadLink'])} style={{ textDecoration: 'none' }}>
                  {' '}
                  Download{' '}
                </a>
                |
                <a href="#" onClick={() => handleDelete(photos[key]['photo_id'])} style={{ textDecoration: 'none' }}>
                  {' '}
                  Delete{' '}
                </a>
              </CardContent>
            </Card>
          </Grid>
        ))}
      </Grid>
    </div>
  );
}
