import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth-service';

@Component({
  selector: 'app-navbar-component',
  imports: [CommonModule, RouterLink],
  templateUrl: './navbar-component.html',
})
export class NavbarComponent {
  isMobileMenuOpen = false;
  isProfileMenuOpen = false;
  authService = inject(AuthService);
  router = inject(Router);

  toggleMobileMenu() {
    this.isMobileMenuOpen = !this.isMobileMenuOpen;
  }

  toggleProfileMenu() {
    this.isProfileMenuOpen = !this.isProfileMenuOpen;
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
