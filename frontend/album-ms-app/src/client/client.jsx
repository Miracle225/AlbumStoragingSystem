import axios from 'axios';
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;
const FULL_API_URL = `${API_BASE_URL}/api/v1`;


const fetchGetData = (uri) => {
  const url = `${FULL_API_URL}${uri}`;
  return axios.get(url).catch((error) => {
    console.error('Error fetching data for URL:', url, 'Error', error.message);
    throw error;
  });
};
const fetchPostData = (uri, payload) => {
  const url = `${FULL_API_URL}${uri}`;
  return axios.post(url, payload).catch((error) => {
    console.error('Error fetching data for URL:', url, 'Error', error.message);
    throw error;
  });
};
const fetchPostDataWithAuth = (uri, payload) => {
  const token = localStorage.getItem('token');
  const url = `${FULL_API_URL}${uri}`;
  return axios
    .post(url, payload, {
      headers: {
        accept: '*/*',
        'Content-Type': 'application/json',
        Authorization: `Bearer ${token}`
      }
    })
    .catch((error) => {
      console.error('Error fetching data for URL:', url, 'Error', error.message);
      throw error;
    });
};

const fetchGetDataWithAuth = async (uri) => {
  const token = localStorage.getItem('token');
  const url = `${FULL_API_URL}${uri}`;
  try {
    const response = await axios.get(url, { headers: { Authorization: `Bearer ${token}` } });
    return response;
  } catch (error) {
    console.error('Error fetching data:', error);
  }
};
const fetchPostFileUploadWithAuth = async (uri, formData) => {
  const token = localStorage.getItem('token');
  const url = `${FULL_API_URL}${uri}`;
  try {
    const token = localStorage.getItem('token');
    const response = await axios.post(url, formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
        Authorization: `Bearer ${token}`
      }
    });
    return response;
  } catch (error) {
    console.error('Error fetching data:', error);
  }
};
const fetchGetDataWithAuthArrayBuffer = (uri) => {
  const token = localStorage.getItem('token');
  const url = `${FULL_API_URL}${uri}`;
  try {
    const response = axios.get(url, {
      headers: {
        Authorization: `Bearer ${token}`
      },
      responseType: 'arraybuffer'
    });
    return response;
  } catch (error) {
    console.error('Error fetching data:', error);
  }
};
const fetchPutDataWithAuth = (uri, payload) => {
  const token = localStorage.getItem('token');
  const url = `${FULL_API_URL}${uri}`;
  return axios
    .put(url, payload, {
      headers: {
        accept: '*/*',
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      }
    })
    .catch((error) => {
      console.error('Error fetching data for URL:', url, 'Error', error.message);
      throw error;
    });
};
const fetchDeleteDataWithAuth = async (uri) => {
  const token = localStorage.getItem('token');
  const url = `${FULL_API_URL}${uri}`;
  try {
    const response = await axios.delete(url, { headers: { Authorization: `Bearer ${token}` } });
    return response;
  } catch (error) {
    console.error('Error fetching data:', error);
  }
};

const fetchGetBlobDataWithAuth = async(uri) => {
  const token = localStorage.getItem('token');
  const url = `${FULL_API_URL}${uri}`;
  try {
    const response = await axios.get(url, 
      { headers: { 'Authorization': `Bearer ${token}`,
     },
    responseType: 'blob' });
    return response;
  } catch (error) {
    console.error('Error fetching data:', error);
  }
};

export default fetchGetData;
export {
  fetchPostData,
  fetchPostDataWithAuth,
  fetchGetDataWithAuth,
  fetchPostFileUploadWithAuth,
  fetchGetDataWithAuthArrayBuffer,
  fetchPutDataWithAuth,
  fetchDeleteDataWithAuth,
  fetchGetBlobDataWithAuth
};
