import { Component, inject, OnInit, signal } from '@angular/core';
import { EventModel } from '../../models/event-model';
import { EventService } from '../../services/event-service';

@Component({
  selector: 'app-event-component',
  imports: [],
  templateUrl: './event-component.html',
})
export class EventComponent implements OnInit{

  // HACER COMPROBACION DE SI EL USUARIO ESTA INSCRITO, DESHABILITAR EL BOTON DE INSCRIBIRSE


  
  private eventService = inject(EventService);
  events = signal<EventModel[]>([]);
  loading = signal(true);
  
  
  ngOnInit(): void {
    this.loadEvents();
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

  registration(): void {
    
  }

}
