import React from 'react';
import { Outlet } from 'react-router-dom';
import { Header } from './components/Header';
import './index.css';


function App() {

  return (
    <div className="App">
      <Header /> {/* Coloca o cabeçalho em todas as páginas */}
      <Outlet /> {/* Onde as páginas vão aparecer; abaixo do Header */}
    </div>
  );
}

export default App;