import Typography from '@mui/material/Typography';
import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import Grid from '@mui/material/Grid';
import MainCard from 'components/MainCard';
import { Link, useNavigate } from 'react-router-dom';
import { useEffect, useState } from 'react';
import { fetchGetDataWithAuth } from '../../client/client';
// ==============================|| SAMPLE PAGE ||============================== //

const cardColors = [
  '#264653',
  '#2a9d8f',
  '#e76f51',
  '#0077b6',
  '#00b4d8',
  '#fb8500',
  '#6f1d1b',
  '#5856D6',
  '#007AFF',
  '#577590',
  '#43aa8b',
  '#e9c46a'
];

const getRandomColor = () => {
  const randomIndex = Math.floor(Math.random() * cardColors.length);
  return cardColors[randomIndex];
};


export default function AlbumDynamicGridPage(){
  const [dataArray, setDataArray] = useState([]);
  const navigate = useNavigate();

  useEffect(() => {
    const isLoggedIn = localStorage.getItem('token');
    if (!isLoggedIn) {
      navigate('/login');
      return;
    }

    fetchGetDataWithAuth('/album/getAll')
      .then((res) => {
        console.log('Data received from API:', res.data);
        setDataArray(res.data);
      })
      .catch((err) => {
        console.error('Error fetching albums:', err);
        if (err.response && (err.response.status === 401 || err.response.status === 403)) {
          navigate('/login');
        }
      });
  }, [navigate]); 

  return (
    <Grid container spacing={3}>
      {dataArray.map((data, index) => (
        <Grid item key={index} size={{xs:12, sm:6, md:4, lg:3}}>
          <Link to={`/album/show?id=${data.id}`}>
          <Card
            sx={{
              backgroundColor: getRandomColor(), 
              textAlign: 'center',
              padding: 3, 
              borderRadius: 2, 
              minHeight: '200px', 
              display: 'flex',
              flexDirection: 'column',
              justifyContent: 'center',
              alignItems: 'center', 
              cursor: 'pointer', 
              transition: 'transform 0.2s ease-in-out, box-shadow 0.2s ease-in-out',
              '&:hover': {
                transform: 'scale(1.03)', 
                boxShadow: '0 10px 20px rgba(0,0,0,0.1)', 
              }
            }}
          >
            <CardContent>
              <Typography
                variant="h4"
                component="h2"
                sx={{
                  color: 'white',
                  fontWeight: '600',
                  fontSize: '1.75rem', 
                  wordBreak: 'break-word' 
                }}
              >
                {data.name}
              </Typography>
            </CardContent>
          </Card>
          </Link>
        </Grid>
      ))}
    </Grid>
  );
};


