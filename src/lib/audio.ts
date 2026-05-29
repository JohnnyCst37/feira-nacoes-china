/**
 * Utilitário de Áudio Sino-Brasileiro
 * REMOVIDOS SINTETIZADORES ARTIFICIAIS (conforme feedback do professor).
 * Utiliza apenas áudio real carregado em elementos HTML5 Audio padrão.
 */

class AudioController {
  private bgAudio: HTMLAudioElement | null = null;
  private isMuted: boolean = true;
  private currentUrl: string = '';
  private volume: number = 0.25;

  constructor() {
    if (typeof window !== 'undefined') {
      this.bgAudio = new Audio();
      this.bgAudio.loop = true;
      this.bgAudio.volume = this.volume;
      this.currentUrl = '/audio/traditional_chinese_music.webm';
      this.bgAudio.src = this.currentUrl;
    }
  }

  public setVolume(volume: number) {
    this.volume = volume;
    if (this.bgAudio) {
      this.bgAudio.volume = volume;
    }
  }

  public getVolume(): number {
    return this.volume;
  }

  /**
   * Define e executa a melodia real de fundo (link de áudio MP3 real).
   */
  public setMelodyUrl(url: string) {
    if (!this.bgAudio) return;
    
    // Se a URL for vazia, volta para a música de fundo padrão
    const targetUrl = url || '/audio/traditional_chinese_music.webm';
    
    if (this.currentUrl !== targetUrl) {
      this.currentUrl = targetUrl;
      this.bgAudio.src = targetUrl;
      // Não damos play automático aqui para evitar tocar na Trilha se não desejado.
      // O play deve ser explícito pela página que possui permissão de áudio.
    }
  }

  public playBackground() {
    if (!this.bgAudio) return;
    if (this.currentUrl) {
      this.bgAudio.play().catch(err => console.log("Aguardando interação para áudio:", err));
    }
  }

  public pauseBackground() {
    if (this.bgAudio) {
      this.bgAudio.pause();
    }
  }

  /**
   * Alterna o estado de mute da música de fundo.
   */
  public toggleMute(): boolean {
    this.isMuted = !this.isMuted;
    if (this.bgAudio) {
      if (this.isMuted) {
        this.bgAudio.pause();
      } else {
        // Tenta tocar apenas se for solicitado explicitamente
        this.playBackground();
      }
    }
    return this.isMuted;
  }

  public getMuteState(): boolean {
    return this.isMuted;
  }

  public stopAll() {
    if (this.bgAudio) {
      this.bgAudio.pause();
    }
  }

  // Stubs vazios para evitar quebra de compilação em arquivos que ainda possuam referências temporárias
  public playMysticalGong() {}
  public playWindChimes() {}
  public playTeaPour() {}
  public playPastelCrunch() {}
  public playPipaSound() {}
  public playErhuSound() {}
  public playGuzhengMelody() {}
}

export const audio = new AudioController();
