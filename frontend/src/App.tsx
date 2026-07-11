import { useState, useRef, useEffect } from 'react';
import { Search, Upload, FileText, Send, Database, CheckCircle2, ChevronRight, FileCode, Layers, Loader2 } from 'lucide-react';

export default function App() {
  const [query, setQuery] = useState('');
  const [chat, setChat] = useState<{ role: 'user' | 'assistant', text: string, status?: string }[]>([
    { role: 'assistant', text: 'Olá! Sou o assistente corporativo (RAG Engine). Faça perguntas sobre os manuais técnicos, relatórios financeiros ou políticas de RH indexadas na nossa base de conhecimento vetorial.' }
  ]);
  const [isTyping, setIsTyping] = useState(false);
  const [status, setStatus] = useState('');
  const bottomRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    bottomRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [chat, status]);

  const handleSend = () => {
    if (!query.trim()) return;
    const userText = query;
    setChat(prev => [...prev, { role: 'user', text: userText }]);
    setQuery('');
    setIsTyping(true);
    
    // Simulate RAG Pipeline
    setStatus('Extraindo embeddings da consulta (OpenAI text-embedding-3-small)...');
    
    setTimeout(() => {
      setStatus('Buscando contexto relevante no banco vetorial (pgvector HNSW)...');
      
      setTimeout(() => {
        setStatus('Sintetizando resposta final via GPT-4o-mini com o contexto recuperado...');
        
        setTimeout(() => {
          setStatus('');
          setIsTyping(false);
          setChat(prev => [...prev, { 
            role: 'assistant', 
            text: `Baseado nos documentos corporativos, aqui está a análise sobre "${userText}". O sistema de RAG identificou as principais diretrizes no **Manual de Operações v3.2** (página 45) e no **Relatório Q3** confirmando que os procedimentos técnicos devem seguir a norma ABNT vigente.`,
          }]);
        }, 1500);
      }, 1500);
    }, 1500);
  };

  return (
    <div className="flex h-screen bg-[#0d1117] text-gray-200 font-sans w-full absolute top-0 left-0 right-0 bottom-0">
      {/* Sidebar */}
      <div className="w-72 bg-[#161b22] border-r border-[#30363d] flex flex-col">
        <div className="p-4 border-b border-[#30363d] flex items-center gap-3">
          <div className="w-8 h-8 rounded bg-blue-600 flex items-center justify-center">
            <Database size={16} className="text-white" />
          </div>
          <h1 className="font-semibold text-sm tracking-wide text-white">Corporate RAG</h1>
        </div>
        
        <div className="p-4 flex-1 overflow-y-auto">
          <div className="text-xs font-semibold text-gray-500 uppercase tracking-wider mb-3">Conhecimento Indexado</div>
          
          <div className="space-y-2">
            {[
              { name: 'Manual_Operacoes_v3.pdf', size: '2.4 MB', type: 'PDF' },
              { name: 'Relatorio_Q3_Financeiro.xlsx', size: '1.1 MB', type: 'XLSX' },
              { name: 'Politicas_RH_2026.docx', size: '450 KB', type: 'DOCX' },
              { name: 'Arquitetura_Microservicos.md', size: '12 KB', type: 'MD' },
            ].map(doc => (
              <div key={doc.name} className="flex items-center gap-3 p-2 rounded-md hover:bg-[#21262d] cursor-pointer transition-colors border border-transparent hover:border-[#30363d]">
                <FileText size={16} className={doc.type === 'PDF' ? 'text-red-400' : doc.type === 'XLSX' ? 'text-green-400' : 'text-blue-400'} />
                <div className="flex-1 overflow-hidden">
                  <div className="text-sm truncate text-gray-300">{doc.name}</div>
                  <div className="text-xs text-gray-500 flex items-center gap-1">
                    <CheckCircle2 size={10} className="text-green-500" />
                    Indexado (HNSW) • {doc.size}
                  </div>
                </div>
              </div>
            ))}
          </div>
          
          <button className="mt-4 w-full flex items-center justify-center gap-2 py-2 px-4 rounded-md border border-dashed border-[#30363d] text-gray-400 hover:text-white hover:border-gray-500 transition-colors text-sm">
            <Upload size={14} />
            Ingerir Documento
          </button>
        </div>
        
        <div className="p-4 border-t border-[#30363d] bg-[#0d1117]">
          <div className="flex items-center gap-2 text-xs text-gray-400">
            <Layers size={14} />
            <span>Stack: Java 21, Spring AI, pgvector</span>
          </div>
        </div>
      </div>

      {/* Main Chat Area */}
      <div className="flex-1 flex flex-col bg-[#0d1117] relative">
        <div className="absolute inset-0 overflow-hidden pointer-events-none">
           <div className="absolute -top-[20%] -right-[10%] w-[500px] h-[500px] rounded-full bg-blue-900/20 blur-[120px]" />
        </div>
        
        <div className="flex-1 overflow-y-auto p-8 z-10 flex flex-col gap-6">
          {chat.map((msg, i) => (
            <div key={i} className={`flex gap-4 max-w-3xl ${msg.role === 'user' ? 'ml-auto' : ''}`}>
              {msg.role === 'assistant' && (
                <div className="w-8 h-8 rounded bg-blue-600/20 border border-blue-500/30 flex items-center justify-center shrink-0 mt-1">
                  <FileCode size={16} className="text-blue-400" />
                </div>
              )}
              
              <div className={`p-4 rounded-lg text-sm leading-relaxed ${
                msg.role === 'user' 
                  ? 'bg-blue-600 text-white shadow-md' 
                  : 'bg-[#161b22] border border-[#30363d] text-gray-300 shadow-sm'
              }`}>
                {msg.text}
              </div>
            </div>
          ))}
          
          {isTyping && (
            <div className="flex gap-4 max-w-3xl">
               <div className="w-8 h-8 rounded bg-blue-600/20 border border-blue-500/30 flex items-center justify-center shrink-0 mt-1">
                  <FileCode size={16} className="text-blue-400" />
                </div>
                <div className="p-4 rounded-lg bg-[#161b22] border border-[#30363d] flex flex-col gap-2">
                  <div className="flex items-center gap-3 text-blue-400 text-sm font-medium">
                    <Loader2 size={16} className="animate-spin" />
                    Processando pipeline RAG...
                  </div>
                  <div className="text-xs text-gray-500 font-mono flex items-center gap-2">
                    <ChevronRight size={12} /> {status}
                  </div>
                </div>
            </div>
          )}
          <div ref={bottomRef} />
        </div>
        
        <div className="p-6 pt-0 z-10">
          <div className="max-w-3xl mx-auto relative group">
            <div className="absolute inset-0 bg-gradient-to-r from-blue-500 to-purple-600 rounded-xl blur opacity-20 group-hover:opacity-40 transition-opacity" />
            <div className="relative flex items-center bg-[#161b22] border border-[#30363d] rounded-xl overflow-hidden focus-within:border-blue-500 transition-colors shadow-lg">
              <div className="pl-4 text-gray-500">
                <Search size={18} />
              </div>
              <input 
                type="text" 
                value={query}
                onChange={e => setQuery(e.target.value)}
                onKeyDown={e => e.key === 'Enter' && handleSend()}
                placeholder="Ex: Quais são as regras de compliance no relatório Q3?"
                className="w-full bg-transparent border-none text-sm text-gray-200 p-4 focus:outline-none placeholder-gray-500"
              />
              <button 
                onClick={handleSend}
                disabled={!query.trim() || isTyping}
                className="p-4 text-gray-400 hover:text-blue-400 disabled:opacity-50 transition-colors"
              >
                <Send size={18} />
              </button>
            </div>
          </div>
          <div className="text-center mt-3 text-xs text-gray-600">
             Corporate RAG Engine — UI de demonstração. O processamento real ocorre no backend Java.
          </div>
        </div>
      </div>
    </div>
  );
}
