import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth-service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-register-component',
  imports: [RouterLink, ReactiveFormsModule, CommonModule],
  templateUrl: './register-component.html',
})
export class RegisterComponent implements OnInit {

  registerForm!: FormGroup;
  errorMessage: string | null = null;

  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);

  ngOnInit(): void {
    this.registerForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      firstName: ['', [Validators.required]],
      lastName: ['', [Validators.required]],
    })
  }

  onSubmit(): void {
    this.errorMessage = null;

    if (this.registerForm.valid) {
      this.authService.register(this.registerForm.value).subscribe({
        next: () => { this.router.navigate(['/'])},
        error: (error) => {
          this.errorMessage = "Error al registrar al usuario"
          console.error("Error al registrar al usuario: ", error)
        }
      })
    }
  }
}