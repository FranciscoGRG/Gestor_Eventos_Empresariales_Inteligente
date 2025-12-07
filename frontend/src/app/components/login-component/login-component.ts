import { CommonModule } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth-service';

@Component({
  selector: 'app-login-component',
  imports: [RouterLink, ReactiveFormsModule, CommonModule],
  templateUrl: './login-component.html',
})
export class LoginComponent implements OnInit {

  loginForm!: FormGroup;
  errorMessage: string | null = null;

  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);

  ngOnInit(): void {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
    })
  }

  onSubmit(): void {
    this.errorMessage = null;

    if (this.loginForm.valid) {
      this.authService.login(this.loginForm.value).subscribe({
        next: () => { this.router.navigate(['/'])},
        error: (err) => {
          this.errorMessage = "Usuario o contraseña incorrectos",
          console.error("Login fallido: ", err)
        }
      })
    }
  }
}
