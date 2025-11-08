// assets
import { BookOutlined, FileImageOutlined } from '@ant-design/icons';

// icons
const icons = {
  BookOutlined,
  FileImageOutlined
};

// ==============================|| MENU ITEMS - SAMPLE PAGE & DOCUMENTATION ||============================== //

const albums = {
  id: 'Albums',
  title: 'Albums',
  type: 'group',
  children: [
    {
      id: 'Album',
      title: 'Albums',
      type: 'item',
      url: '/albums',
      icon: icons.BookOutlined,
      end: true
    },
    {
      id: 'AddAlbum',
      title: 'Add album',
      type: 'item',
      url: '/album/add',
      icon: icons.FileImageOutlined
    }
  ]
};

export default albums;
