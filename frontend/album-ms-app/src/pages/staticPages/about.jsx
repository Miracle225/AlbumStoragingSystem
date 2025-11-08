// material-ui
import Typography from '@mui/material/Typography';

// project imports
import MainCard from 'components/MainCard';
// ==============================|| SAMPLE PAGE ||============================== //

export default function SamplePage() {

  return (
    <MainCard title="About">
      <Typography variant="body2">
        It&apos;s about creating my own app using React + Spring Boot
      </Typography>
    </MainCard>
  );
}
