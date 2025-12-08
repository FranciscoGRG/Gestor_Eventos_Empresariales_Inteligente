import { Component, inject, OnInit, signal } from '@angular/core';
import { EventModel } from '../../models/event-model';
import { EventService } from '../../services/event-service';
import { RegistrationService } from '../../services/registration-service';
import { RegistrationModel } from '../../models/registration-model';

@Component({
  selector: 'app-event-component',
  imports: [],
  templateUrl: './event-component.html',
})
export class EventComponent implements OnInit{

  // HACER COMPROBACION DE SI EL USUARIO ESTA INSCRITO, DESHABILITAR EL BOTON DE INSCRIBIRSE


  
  private eventService = inject(EventService);
  private registrationService = inject(RegistrationService);
  events = signal<EventModel[]>([]);
  registrations = signal<RegistrationModel[]>([]);
  loading = signal(true);
  
  
  ngOnInit(): void {
    this.loadEvents();
    this.loadRegistrations();
  }


  loadEvents(): void {
    this.eventService.getAllPublishedAndActiveEvents().subscribe({
      next: (events) => {
        this.events.set(events);
        this.loading.set(false);
      },
      error: (error) => {
        this.events.set([]);
        this.loading.set(false);
        console.error("Error al cargar los eventos: ", error);
      }
    })
  }

  registration(eventId: number): void {
    this.registrationService.createRegistration(eventId).subscribe({
      next: () => {
        alert("Inscrito correctamente");
        this.loadEvents();
      },
      error: (error) => {
        console.error("Error al inscribirse al evento: ", error);
      }
    })
  }

  loadRegistrations(): void {
    this.registrationService.getAllRegistrationsByUserId().subscribe({
      next: (registrations) => {
        this.registrations.set(registrations);
        this.loading.set(false);
      },
      error: (error) => {
        this.registrations.set([]);
        this.loading.set(false);
        console.error("Error al cargar las inscripciones: ", error);
      }
    })
  }

  isUserRegistered(eventId: number): boolean {
    return this.registrations().some((registration) => registration.eventId === eventId);
  }
}
