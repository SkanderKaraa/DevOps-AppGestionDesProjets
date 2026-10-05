package tn.esprit.backend.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.backend.entity.Entreprise;
import tn.esprit.backend.repository.EntrepriseRepository;
import tn.esprit.backend.service.impl.EntrepriseServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EntrepriseServiceImplTest {

    @Mock
    EntrepriseRepository entrepriseRepository;

    @InjectMocks
    EntrepriseServiceImpl service;

    private Entreprise creer(Long id, String nom) {
        Entreprise e = new Entreprise();
        e.setId(id);
        e.setNom(nom);
        e.setAdresse("Tunis");
        return e;
    }

    @Test
    void addEntreprise_doitSauvegarderEtRetourner() {
        Entreprise e = creer(null, "ESPRIT");
        when(entrepriseRepository.save(e)).thenReturn(e);

        Entreprise result = service.addEntreprise(e);

        assertEquals("ESPRIT", result.getNom());
        verify(entrepriseRepository, times(1)).save(e);
    }

    @Test
    void getEntrepriseById_existante_doitRetournerEntreprise() {
        Entreprise e = creer(1L, "ESPRIT");
        when(entrepriseRepository.findById(1L)).thenReturn(Optional.of(e));

        Entreprise result = service.getEntrepriseById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getEntrepriseById_inexistante_doitRetournerNull() {
        when(entrepriseRepository.findById(99L)).thenReturn(Optional.empty());

        assertNull(service.getEntrepriseById(99L));
    }

    @Test
    void getAllEntreprises_doitRetournerLaListe() {
        when(entrepriseRepository.findAll())
                .thenReturn(List.of(creer(1L, "A"), creer(2L, "B")));

        assertEquals(2, service.getAllEntreprises().size());
    }

    @Test
    void updateEntreprise_doitModifierLeNom() {
        Entreprise e = creer(1L, "Ancien");
        e.setNom("Nouveau");
        when(entrepriseRepository.save(e)).thenReturn(e);

        assertEquals("Nouveau", service.updateEntreprise(e).getNom());
    }

    @Test
    void deleteEntreprise_doitAppelerDeleteById() {
        service.deleteEntreprise(1L);

        verify(entrepriseRepository, times(1)).deleteById(1L);
    }
}
