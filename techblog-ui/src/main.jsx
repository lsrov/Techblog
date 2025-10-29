import React from 'react'
import ReactDOM from 'react-dom/client'
import { createBrowserRouter, RouterProvider } from 'react-router-dom';
import App from './App.jsx'

// Todas as páginas do site
import { HomePage } from './pages/HomePage.jsx';
import { LoginPage } from './pages/LoginPage.jsx';
import { ArticleListPage } from './pages/ArticleListPage.jsx';
import { NewArticlePage } from './pages/NewArticlePage.jsx';
import { ProtectedRoute } from './components/ProtectedRoute.jsx';
import { ArticleDetailPage } from './pages/ArticleDetailPage.jsx';
import { EditArticlePage } from './pages/EditArticlePage.jsx';

const router = createBrowserRouter([ // Rotas do site
  {
    path: "/",
    element: <App />, 
    children: [
      // Páginas que todos podem ver
      { path: "/", element: <HomePage /> },
      { path: "/articles", element: <ArticleListPage /> },
      { path: "/login", element: <LoginPage /> },
      { 
        path: "/article/:id", // O :id significa que pode ser qualquer número dos que estão no bd
        element: <ArticleDetailPage />,
      },

      // Só vê se estiver logado
      {
        element: <ProtectedRoute />, 
        children: [
          { path: "/new", element: <NewArticlePage /> },
        ]
      },

      // Editar o artigo
      {
        path: "/edit/:id",
        element: <EditArticlePage />,
      },
    ],
  },
]);

ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <RouterProvider router={router} />
  </React.StrictMode>,
)