import {Component, OnInit} from '@angular/core';
import {Observable} from 'rxjs';
import {HttpClient} from '@angular/common/http';
import {ActivatedRoute, Router} from '@angular/router';
import {ClientDTO, CountryDTO} from './model';

@Component({
  selector: 'app-client-edit',
  templateUrl: './client-edit.component.html',
})
export class ClientEditComponent implements OnInit {
  client: ClientDTO;
  countries: Observable<CountryDTO[]>;
  error: any;

  constructor(
    private httpClient: HttpClient,
    private routes: Router,
    private route: ActivatedRoute) {
  }

  ngOnInit(): void {
    this.getClient();
    this.countries = this.getCountries();
  }

  saveClient(): void {
    const url = '/api/client';
    let req: Observable<ClientDTO>;
    if (!this.client.id) {
      req = this.httpClient.post<ClientDTO>(url, this.client);
    } else {
      req = this.httpClient.put<ClientDTO>(url, this.client);
    }

    req.subscribe(response => {
        this.error = null;
        this.routes.navigateByUrl(`/edit/${response.id}`);
      },
      error => this.error = JSON.stringify(error.error)
    );
  }

  private getClient(): void {
    const id = +this.route.snapshot.paramMap.get('id');
    if (!id) {
      this.client = {};
      return;
    }
    this.getClientRest(id)
      .subscribe(it => this.client = it);
  }

  private getClientRest(clientId?: number): Observable<ClientDTO> {
    return this.httpClient.get<ClientDTO>(`/api/client/${clientId}`,
      {
        observe: 'body',
      }
    );
  }

  private getCountries(): Observable<CountryDTO[]> {
    return this.httpClient.get<CountryDTO[]>(`/api/country`,
      {
        observe: 'body',
      }
    );
  }

}


