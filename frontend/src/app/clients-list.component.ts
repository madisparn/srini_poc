import {Component, OnInit} from '@angular/core';
import {Observable} from 'rxjs';
import {HttpClient} from '@angular/common/http';
import {Router} from '@angular/router';
import {ClientDTO} from './model';

@Component({
  selector: 'app-client-list',
  templateUrl: './clients-list.component.html',
})
export class ClientsListComponent implements OnInit  {
  clients: Observable<Array<ClientDTO>>;
  constructor(private httpClient: HttpClient, private router: Router) { }

  ngOnInit(): void {
    this.clients = this.getClients();
  }

  private getClients(): Observable<Array<ClientDTO>> {
    return this.httpClient.get<Array<ClientDTO>>(`/api/client`,
      {
        observe: 'body',
      }
    );
  }

}


