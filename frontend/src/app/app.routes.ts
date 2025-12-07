import { Routes } from '@angular/router';
import { LandingPageComponent } from './components/landing-page-component/landing-page-component';
import { LoginComponent } from './components/login-component/login-component';
import { RegisterComponent } from './components/register-component/register-component';

export const routes: Routes = [
    {path: '', component: LandingPageComponent},
    {path: 'login', component: LoginComponent},
    {path: 'register', component: RegisterComponent}
];
